package edu.icesi.emprendimientos.entity;

import edu.icesi.emprendimientos.entity.keys.RolPermissionId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public Permission getPermission() {
        return permission;
    }
}
