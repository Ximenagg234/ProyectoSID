package edu.icesi.emprendimientos.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UsuarioRolId implements Serializable {

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "id_rol")
    private Integer idRol;

    public UsuarioRolId() {}

    public UsuarioRolId(Integer idUsuario, Integer idRol) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof UsuarioRolId that) {
            return Objects.equals(idUsuario, that.idUsuario) &&
                    Objects.equals(idRol, that.idRol);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idRol);
    }
}
