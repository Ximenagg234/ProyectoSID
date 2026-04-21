package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UsuarioRol> usuarios;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RolPermission> permisos;

    public Rol() {}

    public Rol(Integer idRol, String nombre, List<UsuarioRol> usuarios, List<RolPermission> permisos) {
        this.idRol = idRol;
        this.nombre = nombre;
        this.usuarios = usuarios;
        this.permisos = permisos;
    }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<UsuarioRol> getUsuarios() { return usuarios; }
    public void setUsuarios(List<UsuarioRol> usuarios) { this.usuarios = usuarios; }

    public List<RolPermission> getPermisos() { return permisos; }
    public void setPermisos(List<RolPermission> permisos) { this.permisos = permisos; }
}
