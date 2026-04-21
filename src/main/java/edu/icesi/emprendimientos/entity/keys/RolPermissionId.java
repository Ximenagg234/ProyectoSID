package edu.icesi.emprendimientos.entity.keys;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RolPermissionId implements Serializable {

    private Integer idRol;
    private Integer idPermission;

    public RolPermissionId() {}

    public RolPermissionId(Integer idRol, Integer idPermission) {
        this.idRol = idRol;
        this.idPermission = idPermission;
    }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public Integer getIdPermission() { return idPermission; }
    public void setIdPermission(Integer idPermission) { this.idPermission = idPermission; }

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
