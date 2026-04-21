package edu.icesi.emprendimientos.entity;

import edu.icesi.emprendimientos.entity.keys.UsuarioRolId;
import jakarta.persistence.*;

@Entity
@Table(name = "user_role")
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

    public UsuarioRol() {}

    public UsuarioRol(UsuarioRolId id, Usuario usuario, Rol rol) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
    }

    public UsuarioRolId getId() { return id; }
    public void setId(UsuarioRolId id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getClave() { return usuario != null ? usuario.getClave() : null; }
    public String getNombreCompleto() { return usuario != null ? usuario.getNombreCompleto() : null; }
}
