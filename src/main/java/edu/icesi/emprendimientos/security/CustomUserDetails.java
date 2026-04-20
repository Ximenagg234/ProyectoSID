package edu.icesi.emprendimientos.security;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.entity.UsuarioRol;
import edu.icesi.emprendimientos.entity.RolPermission;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UsuarioRol ur : usuario.getRoles()) {

            String roleName = "ROLE_" + ur.getRol().getNombre();
            authorities.add(new SimpleGrantedAuthority(roleName));

            for (RolPermission rp : ur.getRol().getPermisos()) {
                authorities.add(
                        new SimpleGrantedAuthority(rp.getPermission().getNombre())
                );
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getClave();
    }

    @Override
    public String getUsername() {
        return usuario.getNombreCompleto();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}