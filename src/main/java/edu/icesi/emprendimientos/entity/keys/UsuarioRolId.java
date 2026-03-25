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
