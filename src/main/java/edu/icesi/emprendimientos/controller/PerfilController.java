package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;

    public PerfilController(UsuarioRepository usuarioRepository,
                            FileStorageService fileStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.fileStorageService = fileStorageService;
    }

    /** Página de perfil */
    @GetMapping("/perfil")
    public String perfil(Authentication auth, Model model) {
        Usuario usuario = getUsuario(auth);
        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "perfil/index :: contenido");
        return "layout";
    }

    /**
     * Recibe la foto recortada como data-URL base64 desde JavaScript,
     * la guarda en disco y actualiza el campo foto_perfil del usuario.
     * Devuelve JSON { "url": "/uploads/perfiles/xxx.png" }
     */
    @PostMapping("/perfil/foto")
    @ResponseBody
    public ResponseEntity<?> actualizarFoto(@RequestBody Map<String, String> body,
                                             Authentication auth) {
        try {
            String dataUrl = body.get("foto");
            if (dataUrl == null || dataUrl.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No se recibió imagen"));
            }
            String url = fileStorageService.guardarBase64(dataUrl, "perfiles");

            Usuario usuario = getUsuario(auth);
            usuario.setFotoPerfil(url);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al guardar la foto: " + e.getMessage()));
        }
    }

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByCorreoInstitucional(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
