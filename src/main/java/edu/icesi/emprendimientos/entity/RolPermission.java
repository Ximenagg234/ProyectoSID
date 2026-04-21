package edu.icesi.emprendimientos.entity;

import edu.icesi.emprendimientos.entity.keys.RolPermissionId;
import jakarta.persistence.*;

@Entity
@Table(name = "role_permission")
public class RolPermission {

    @EmbeddedId
    private RolPermissionId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idPermission")
    @JoinColumn(name = "id_permission")
    private Permission permission;

    public RolPermission() {}

    public RolPermission(RolPermissionId id, Rol rol, Permission permission) {
        this.id = id;
        this.rol = rol;
        this.permission = permission;
    }

    public RolPermissionId getId() { return id; }
    public void setId(RolPermissionId id) { this.id = id; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
}
