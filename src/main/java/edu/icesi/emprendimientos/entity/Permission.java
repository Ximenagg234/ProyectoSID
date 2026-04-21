package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Integer idPermission;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolPermission> roles;

    public Permission() {}

    public Permission(Integer idPermission, String nombre, String descripcion, List<RolPermission> roles) {
        this.idPermission = idPermission;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.roles = roles;
    }

    public Integer getIdPermission() { return idPermission; }
    public void setIdPermission(Integer idPermission) { this.idPermission = idPermission; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<RolPermission> getRoles() { return roles; }
    public void setRoles(List<RolPermission> roles) { this.roles = roles; }
}
