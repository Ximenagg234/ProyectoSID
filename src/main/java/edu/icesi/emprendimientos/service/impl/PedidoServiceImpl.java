package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.mongo.document.PedidoDocument;
import edu.icesi.emprendimientos.mongo.service.MongoSyncService;
import edu.icesi.emprendimientos.mongo.service.StockService;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.repository.PedidoRepository;
import edu.icesi.emprendimientos.service.PedidoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;
    private final MongoSyncService mongoSync;
    private final StockService     stockService;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             EstadoRepository estadoRepository,
                             MongoSyncService mongoSync,
                             StockService stockService) {
        this.pedidoRepository = pedidoRepository;
        this.estadoRepository = estadoRepository;
        this.mongoSync        = mongoSync;
        this.stockService     = stockService;
    }

    @Override
    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new RuntimeException("El pedido debe tener productos");
        }

        // Calcular total
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido d : pedido.getDetalles()) {
            d.setPedido(pedido);
            total = total.add(d.getSubtotal());
        }
        pedido.setTotal(total);

        // Verificar stock ANTES de guardar en PostgreSQL
        if (pedido.getEmprendimiento() != null) {
            Map<Integer, Integer> stockItems = new LinkedHashMap<>();
            pedido.getDetalles().forEach(d -> {
                if (d.getProducto() != null) {
                    stockItems.merge(d.getProducto().getIdProducto(), d.getCantidad(), Integer::sum);
                }
            });
            // Verificación de stock (lanza excepción si no hay suficiente)
            try {
                stockService.verificarStock(
                        pedido.getEmprendimiento().getIdEmprendimiento(), stockItems);
            } catch (IllegalStateException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Guardar en PostgreSQL
        Pedido saved = pedidoRepository.save(pedido);

        // Dual-write a MongoDB + decrementar stock atómicamente
        if (pedido.getEmprendimiento() != null) {
            Map<Integer, Integer> stockItems = new LinkedHashMap<>();
            pedido.getDetalles().forEach(d -> {
                if (d.getProducto() != null) {
                    stockItems.merge(d.getProducto().getIdProducto(), d.getCantidad(), Integer::sum);
                }
            });
            try {
                PedidoDocument docSincronizado = mongoSync.sincronizarPedido(saved);
                stockService.crearPedidoTransaccional(docSincronizado, stockItems);
            } catch (Exception e) {
                // Log pero no bloquea — el pedido en PostgreSQL ya fue guardado
                // En producción esto debería usar un patrón Saga o compensación
            }
        }

        return saved;
    }

    @Override
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido buscarPorId(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
    }

    @Override
    public List<Pedido> listarPorUsuario(Integer idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Pedido> listarPorEmprendedor(Integer idUsuario) {
        return pedidoRepository.findByEmprendimiento_Usuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Pedido> listarRecibidosPorEmprendedor(Integer idUsuario) {
        return pedidoRepository.findByEmprendimiento_Usuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public void actualizarEstado(Integer idPedido, Integer idEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
        Estado estado = estadoRepository.findById(idEstado)
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado"));
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);

        // Si se cancela, restaurar stock en MongoDB
        try {
            if ("CANCELADO".equalsIgnoreCase(estado.getNombre())) {
                mongoSync.sincronizarPedido(pedido);
                pedidoRepository.findById(idPedido).ifPresent(p -> {
                    try {
                        PedidoDocument doc = mongoSync.sincronizarPedido(p);
                        stockService.restaurarStockPorCancelacion(doc);
                    } catch (Exception ex) { /* no bloquea */ }
                });
            } else {
                mongoSync.sincronizarPedido(pedido);
            }
        } catch (Exception e) { /* no bloquea */ }
    }

    @Override
    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }
}
