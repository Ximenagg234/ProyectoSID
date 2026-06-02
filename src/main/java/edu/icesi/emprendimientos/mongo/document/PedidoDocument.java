package edu.icesi.emprendimientos.mongo.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Documento de Pedidos en MongoDB.
 * Un pedido pertenece a UN emprendimiento con sus detalles embebidos.
 * Cuando el carrito tiene productos de varios emprendimientos,
 * se generan pedidos separados (uno por emprendimiento).
 *
 * Estados posibles: PENDIENTE | CONFIRMADO | PREPARANDO | ENTREGADO | CANCELADO
 * (distintos a estados de emprendimiento: ACTIVO | INACTIVO)
 * Colección: Pedidos (base de datos: Sid)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Pedidos")
public class PedidoDocument {

    @Id
    private String id;

    /** Referencia al ID del pedido en PostgreSQL */
    @Indexed(unique = true)
    @Field("id_pedido_sql")
    private Integer idPedidoSql;

    /** Referencia al ObjectId del emprendimiento en MongoDB */
    @Indexed
    @Field("emprendimiento_id")
    private String emprendimientoId;

    @Field("id_emprendimiento_sql")
    private Integer idEmprendimientoSql;

    @Field("nombre_emprendimiento")
    private String nombreEmprendimiento;

    /** Datos del comprador (embebido para evitar JOIN con PostgreSQL) */
    private CompradorEmbed comprador;

    /**
     * Estado del pedido — separado del estado del emprendimiento.
     * Valores: PENDIENTE | CONFIRMADO | PREPARANDO | ENTREGADO | CANCELADO
     */
    private String estado;

    /** Detalles del pedido — embebidos porque son parte integral del pedido */
    private List<DetallePedidoEmbed> detalles;

    private BigDecimal total;

    @Field("fecha_pedido")
    private Date fechaPedido;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    // ── Embedded classes ──────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CompradorEmbed {
        @Field("id_usuario_sql")
        private Integer idUsuarioSql;
        @Field("nombre_usuario")
        private String nombreUsuario;
        private String correo;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class DetallePedidoEmbed {
        @Field("id_detalle_sql")
        private Integer idDetalleSql;
        @Field("id_producto_sql")
        private Integer idProductoSql;
        @Field("nombre_producto")
        private String nombreProducto;
        private Integer cantidad;
        @Field("precio_unitario")
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
