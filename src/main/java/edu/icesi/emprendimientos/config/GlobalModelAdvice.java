package edu.icesi.emprendimientos.config;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioRepository usuarioRepository;

    public GlobalModelAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("currentUser")
    public Usuario currentUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return usuarioRepository.findByCorreoInstitucional(auth.getName()).orElse(null);
    }
}
