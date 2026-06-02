package edu.icesi.emprendimientos.mongo.document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * Documento de sincronización de usuarios en MongoDB.
 * Contiene una versión desnormalizada y ligera del usuario para
 * ser embebida en emprendimientos, pedidos y calificaciones sin
 * necesidad de consultar PostgreSQL.
 *
 * La fuente de verdad de usuarios sigue siendo PostgreSQL.
 * Este documento se actualiza mediante sincronización cuando
 * el usuario modifica su perfil.
 * Colección: Usuarios (base de datos: Sid)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Usuarios")
public class UsuarioSyncDocument {

    @Id
    private String id;

    /** ID del usuario en PostgreSQL — clave de sincronización */
    @Indexed(unique = true)
    @Field("id_usuario_sql")
    private Integer idUsuarioSql;

    @Field("nombre_completo")
    private String nombreCompleto;

    @Indexed(unique = true)
    @Field("correo_institucional")
    private String correoInstitucional;

    @Field("programa_academico")
    private String programaAcademico;

    /** Roles del usuario (ADMIN, EMPRENDEDOR, COMPRADOR) */
    private List<String> roles;

    @Field("foto_perfil")
    private String fotoPerfil;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;
}
