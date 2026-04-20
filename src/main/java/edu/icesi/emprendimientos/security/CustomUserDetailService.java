package edu.icesi.emprendimientos.security;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        Usuario usuario = usuarioRepository.findByNombreCompleto(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User
                .withUsername(usuario.getNombreCompleto())
                .password("{noop}" + usuario.getClave())
                .authorities("ROLE_ADMIN")
                .build();
    }
}