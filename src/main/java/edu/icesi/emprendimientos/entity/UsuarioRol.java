package edu.icesi.emprendimientos.entity;

import edu.icesi.emprendimientos.entity.keys.UsuarioRolId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {

    @EmbeddedId
    private UsuarioRolId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

    public Rol getRol() {
        return rol;
    }

    public String getClave() {
        return usuario != null ? usuario.getClave() : null;
    }

    public String getNombreCompleto() {
        return usuario != null ? usuario.getNombreCompleto() : null;
    }
}
