package edu.icesi.emprendimientos.entity.keys;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolPermissionId implements Serializable {

    private Integer idRol;
    private Integer idPermission;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RolPermissionId that) {
            return Objects.equals(idRol, that.idRol) &&
                    Objects.equals(idPermission, that.idPermission);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol, idPermission);
    }
}