package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.service.RolService;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/list";
    }

    // FORM CREAR
    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    // GUARDAR
    @PostMapping
    public String guardar(@ModelAttribute Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuarios";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        return "usuarios/form";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    // ASIGNAR ROLES (FORM)
    @GetMapping("/asignar-roles/{id}")
    public String asignarRoles(@PathVariable Integer id, Model model) {

        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", rolService.listar());

        return "usuarios/asignar-roles";
    }

    // GUARDAR ASIGNACIÓN
    @PostMapping("/asignar-roles")
    public String guardarRol(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idRol
    ) {

        usuarioService.asignarRol(idUsuario, idRol);
        return "redirect:/usuarios";
    }

    // QUITAR ROL
    @GetMapping("/quitar-rol")
    public String quitarRol(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idRol
    ) {

        usuarioService.quitarRol(idUsuario, idRol);
        return "redirect:/usuarios";
    }
}