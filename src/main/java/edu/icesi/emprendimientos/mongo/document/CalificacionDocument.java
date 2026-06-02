package edu.icesi.emprendimientos.mongo.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Documento de Calificaciones en MongoDB.
 * Colección separada (no embebida) para permitir queries
 * independientes sobre calificaciones sin cargar el emprendimiento.
 * Colección: Calificaciones (base de datos: Sid)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Calificaciones")
public class CalificacionDocument {

    @Id
    private String id;

    /** Referencia al ID de la calificacion en PostgreSQL */
    @Field("id_calificacion_sql")
    private Integer idCalificacionSql;

    /** Referencia al emprendimiento en MongoDB (ObjectId como String) */
    @Indexed
    @Field("emprendimiento_id")
    private String emprendimientoId;

    /** Referencia al ID del emprendimiento en PostgreSQL */
    @Field("id_emprendimiento_sql")
    private Integer idEmprendimientoSql;

    /** Referencia al ID del pedido en PostgreSQL */
    @Field("id_pedido_sql")
    private Integer idPedidoSql;

    /** Datos del usuario que calificó (embebido para evitar JOIN) */
    private UsuarioEmbed usuario;

    private Integer puntuacion;

    private String comentario;

    private Date fecha;

    @Field("created_at")
    private Date createdAt;

    // ── Embedded class ────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UsuarioEmbed {
        @Field("id_usuario_sql")
        private Integer idUsuarioSql;
        @Field("nombre_usuario")
        private String nombreUsuario;
    }
}
