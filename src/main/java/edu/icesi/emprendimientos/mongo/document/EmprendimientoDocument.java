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
 * Documento principal de Emprendimientos en MongoDB.
 * Contiene productos embebidos, métricas y últimas calificaciones.
 * Colección: Emprendimientos (base de datos: Sid)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Emprendimientos")
public class EmprendimientoDocument {

    @Id
    private String id;

    /** Referencia al ID del emprendimiento en PostgreSQL */
    @Indexed(unique = true)
    @Field("id_emprendimiento_sql")
    private Integer idEmprendimientoSql;

    private String nombre;

    @Field("nombre_emprendimiento")
    private String nombreEmprendimiento;

    private String descripcion;

    @Field("logo_url")
    private String logoUrl;

    /** "ACTIVO" | "INACTIVO" — estado propio de emprendimiento */
    private String estado;

    private Boolean destacado;

    /** Datos del emprendedor (embebido, evita JOIN con PostgreSQL) */
    private EmprendedorEmbed emprendedor;

    /** Categoría del emprendimiento (embebido, dato semi-estático) */
    private CategoriaEmbed categoria;

    /** Semestre académico (embebido, dato estático) */
    private SemestreEmbed semestre;

    /**
     * Productos del emprendimiento — embebidos porque siempre
     * se acceden junto al emprendimiento y cambian frecuentemente.
     */
    private List<ProductoEmbed> productos;

    /**
     * Métricas agregadas — actualizadas por triggers/eventos
     * cuando llegan nuevos pedidos o calificaciones.
     */
    private MetricasEmbed metricas;

    /**
     * Cache de las últimas 5 calificaciones recibidas.
     * Permite mostrar reseñas recientes sin query extra.
     */
    @Field("ultimas_calificaciones")
    private List<UltimaCalificacionEmbed> ultimasCalificaciones;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    // ── Embedded classes ──────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class EmprendedorEmbed {
        @Field("id_usuario_sql")
        private Integer idUsuarioSql;
        @Field("nombre_usuario")
        private String nombreUsuario;
        private String correo;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class CategoriaEmbed {
        @Field("id_categoria_sql")
        private Integer idCategoriaSql;
        private String nombre;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SemestreEmbed {
        @Field("id_semestre_sql")
        private Integer idSemestreSql;
        private String nombre;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProductoEmbed {
        @Field("id_producto_sql")
        private Integer idProductoSql;
        private String nombre;
        private String descripcion;
        private BigDecimal precio;
        @Field("stock_disponible")
        private Integer stockDisponible;
        /** "ACTIVO" | "INACTIVO" — estado propio de producto */
        private String estado;
        /** URLs de imágenes del producto */
        private List<String> imagenes;
        @Field("created_at")
        private Date createdAt;
        @Field("updated_at")
        private Date updatedAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class MetricasEmbed {
        @Field("total_pedidos")
        private Integer totalPedidos = 0;
        @Field("ingresos_acumulados")
        private BigDecimal ingresosAcumulados = BigDecimal.ZERO;
        @Field("total_calificaciones")
        private Integer totalCalificaciones = 0;
        @Field("suma_puntuaciones")
        private Integer sumaPuntuaciones = 0;
        @Field("calificacion_promedio")
        private Double calificacionPromedio = 0.0;
        @Field("pedidos_por_mes")
        private List<PedidoMesEmbed> pedidosPorMes;
        @Field("productos_mas_vendidos")
        private List<ProductoVendidoEmbed> productosMasVendidos;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PedidoMesEmbed {
        private String mes;   // "2026-01"
        private Integer total;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ProductoVendidoEmbed {
        @Field("id_producto_sql")
        private Integer idProductoSql;
        private String nombre;
        @Field("unidades_vendidas")
        private Integer unidadesVendidas;
        private BigDecimal ingresos;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UltimaCalificacionEmbed {
        @Field("id_calificacion_sql")
        private Integer idCalificacionSql;
        @Field("id_usuario_sql")
        private Integer idUsuarioSql;
        @Field("nombre_usuario")
        private String nombreUsuario;
        private Integer puntuacion;
        private String comentario;
        private Date fecha;
    }
}
