package edu.icesi.emprendimientos.mongo.service;

import edu.icesi.emprendimientos.mongo.document.EmprendimientoDocument;
import edu.icesi.emprendimientos.mongo.document.PedidoDocument;
import edu.icesi.emprendimientos.mongo.repository.EmprendimientoMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.PedidoMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de stock con garantías ACID en MongoDB.
 *
 * Problema que resuelve: concurrencia en la compra del mismo producto.
 * Si dos usuarios intentan comprar el último item simultáneamente,
 * solo uno tendrá éxito — el otro recibirá "stock insuficiente".
 *
 * Estrategia:
 * - Operaciones atómicas a nivel de documento (findAndModify con condición)
 *   para decrementar stock solo si hay suficiente disponible.
 * - Transacciones MongoDB (@Transactional) para operaciones multi-documento
 *   que deben ser atómicas: decrementar stock + crear pedido.
 *
 * Propiedad ACID garantizada:
 * - Atomicidad: o se decrementa el stock Y se crea el pedido, o nada.
 * - Consistencia: el stock nunca queda negativo.
 * - Aislamiento: findAndModify es atómico, no hay condición de carrera.
 * - Durabilidad: MongoDB Atlas persiste con write concern majority.
 */
@Service
public class StockService {

    private final MongoTemplate         mongoTemplate;
    private final EmprendimientoMongoRepository empRepo;
    private final PedidoMongoRepository pedidoRepo;

    public StockService(MongoTemplate mongoTemplate,
                        EmprendimientoMongoRepository empRepo,
                        PedidoMongoRepository pedidoRepo) {
        this.mongoTemplate = mongoTemplate;
        this.empRepo       = empRepo;
        this.pedidoRepo    = pedidoRepo;
    }

    /**
     * Verifica si hay stock suficiente para una lista de items.
     * No modifica el stock — solo consulta.
     *
     * @param idEmprendimientoSql ID del emprendimiento en PostgreSQL
     * @param items               Mapa de idProductoSql → cantidad requerida
     * @throws IllegalStateException si algún producto no tiene stock suficiente
     */
    public void verificarStock(Integer idEmprendimientoSql,
                               Map<Integer, Integer> items) {
        EmprendimientoDocument emp = empRepo
                .findByIdEmprendimientoSql(idEmprendimientoSql)
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado"));

        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            Integer idProducto = entry.getKey();
            Integer cantidad   = entry.getValue();

            EmprendimientoDocument.ProductoEmbed producto = emp.getProductos().stream()
                    .filter(p -> idProducto.equals(p.getIdProductoSql()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Producto " + idProducto + " no encontrado"));

            if (!"ACTIVO".equalsIgnoreCase(producto.getEstado())) {
                throw new IllegalStateException(
                        "El producto '" + producto.getNombre() + "' no está disponible.");
            }

            if (producto.getStockDisponible() < cantidad) {
                throw new IllegalStateException(
                        "Stock insuficiente para '" + producto.getNombre() + "'. " +
                        "Disponible: " + producto.getStockDisponible() +
                        ", solicitado: " + cantidad + ".");
            }
        }
    }

    /**
     * Decrementa el stock de forma ATÓMICA usando findAndModify con condición.
     *
     * La operación MongoDB es equivalente a:
     *   UPDATE producto SET stock = stock - cantidad
     *   WHERE id = ? AND stock >= cantidad
     *
     * Si el stock es insuficiente en el momento de la actualización
     * (condición de carrera), la operación falla y se lanza excepción.
     *
     * @return true si el decremento fue exitoso
     */
    public boolean decrementarStockAtomico(Integer idEmprendimientoSql,
                                           Integer idProductoSql,
                                           int cantidad) {
        // Busca el documento donde el array de productos tenga el item
        // con stock suficiente, y decrementa en una sola operación atómica
        Query query = new Query(Criteria.where("id_emprendimiento_sql")
                .is(idEmprendimientoSql)
                .and("productos")
                .elemMatch(Criteria.where("id_producto_sql").is(idProductoSql)
                        .and("stock_disponible").gte(cantidad)
                        .and("estado").is("ACTIVO")));

        Update update = new Update()
                .inc("productos.$.stock_disponible", -cantidad)
                .set("updated_at", new Date());

        // findAndModify retorna null si la condición no se cumplió
        EmprendimientoDocument resultado = mongoTemplate.findAndModify(
                query, update, EmprendimientoDocument.class);

        return resultado != null;
    }

    /**
     * Crea un pedido completo de forma transaccional (ACID multi-documento).
     *
     * Garantiza que:
     * 1. El stock de TODOS los productos se decrementa
     * 2. El pedido se crea en MongoDB
     * Solo si AMBAS operaciones tienen éxito. Si una falla, hace rollback.
     *
     * @Transactional usa el MongoTransactionManager configurado
     */
    @Transactional("mongoTransactionManager")
    public PedidoDocument crearPedidoTransaccional(PedidoDocument pedido,
                                                    Map<Integer, Integer> stockItems) {
        // Paso 1: Decrementar stock de cada producto (atómico por item)
        for (Map.Entry<Integer, Integer> entry : stockItems.entrySet()) {
            boolean exitoso = decrementarStockAtomico(
                    pedido.getIdEmprendimientoSql(),
                    entry.getKey(),
                    entry.getValue());

            if (!exitoso) {
                // Si falla, @Transactional hace rollback de todos los decrementos anteriores
                throw new IllegalStateException(
                        "Stock insuficiente para el producto ID: " + entry.getKey() +
                        ". El pedido no fue procesado.");
            }
        }

        // Paso 2: Crear el pedido (dentro de la misma transacción)
        pedido.setCreatedAt(new Date());
        pedido.setUpdatedAt(new Date());
        return pedidoRepo.save(pedido);
    }

    /**
     * Restaura stock cuando un pedido es CANCELADO.
     * También transaccional para garantizar consistencia.
     */
    @Transactional("mongoTransactionManager")
    public void restaurarStockPorCancelacion(PedidoDocument pedido) {
        if (pedido.getDetalles() == null) return;

        pedido.getDetalles().forEach(detalle -> {
            Query query = new Query(
                    Criteria.where("id_emprendimiento_sql").is(pedido.getIdEmprendimientoSql())
                            .and("productos").elemMatch(
                                    Criteria.where("id_producto_sql").is(detalle.getIdProductoSql())));

            Update update = new Update()
                    .inc("productos.$.stock_disponible", detalle.getCantidad())
                    .set("updated_at", new Date());

            mongoTemplate.findAndModify(query, update, EmprendimientoDocument.class);
        });
    }
}
