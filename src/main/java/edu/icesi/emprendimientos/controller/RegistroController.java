package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RegistroController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public RegistroController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping("/registro")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario,
                            @RequestParam(value = "roles", required = false) List<Integer> roles) {
        Usuario guardado = usuarioService.guardar(usuario);
        if (roles != null) {
            for (Integer idRol : roles) {
                usuarioService.asignarRol(guardado.getIdUsuario(), idRol);
            }
        } else {
            // Por defecto: COMPRADOR (id=3)
            usuarioService.asignarRol(guardado.getIdUsuario(), 3);
        }
        return "redirect:/login?registered";
    }
}
