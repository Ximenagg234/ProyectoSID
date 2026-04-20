package edu.icesi.emprendimientos.security;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreoInstitucional(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // El password debe incluir el ID del encoder: {noop} para texto plano o {bcrypt} para hashes BCrypt
        String password = usuario.getClave();
        if (!password.contains("{")) {
            password = "{noop}" + password;
        }

        return User
                .withUsername(usuario.getCorreoInstitucional())
                .password(password)
                .authorities(
                    usuario.getRoles() != null ?
                    usuario.getRoles().stream()
                        .map(ur -> "ROLE_" + ur.getRol().getNombre())
                        .collect(Collectors.toList())
                        .toArray(new String[0])
                    : new String[0]
                )
                .build();
    }
}