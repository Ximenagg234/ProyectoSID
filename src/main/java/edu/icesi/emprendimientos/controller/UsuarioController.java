package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.service.ProductoService;
import edu.icesi.emprendimientos.service.RolService;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final ProductoService productoService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService, ProductoService productoService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.productoService = productoService;
    }

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("roles", rolService.listar());
        model.addAttribute("contenido", "usuarios/list :: contenido");
        return "layout";
    }

    // FORM CREAR
    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("contenido", "usuarios/form :: contenido");
        return "layout";
    }

    // GUARDAR
    @PostMapping
    public String guardar(@ModelAttribute Usuario usuario) {
        Usuario usuarioGuardado = usuarioService.guardar(usuario);
        return "redirect:/usuarios/asignar-roles/" + usuarioGuardado.getIdUsuario();
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("contenido", "usuarios/form :: contenido");
        return "layout";
    }

    // ELIMINAR USUARIO
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

    // GESTIONAR ROLES (FORM — muestra asignados + disponibles)
    @GetMapping("/asignar-roles/{id}")
    public String asignarRoles(@PathVariable Integer id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", rolService.listar());
        model.addAttribute("contenido", "usuarios/asignar-roles :: contenido");
        return "layout";
    }

    // GUARDAR ASIGNACIÓN DE ROLES
    @PostMapping("/asignar-roles")
    public String guardarRoles(
            @RequestParam Integer idUsuario,
            @RequestParam(value = "roles", required = false) List<Integer> roles
    ) {
        if (roles != null) {
            for (Integer idRol : roles) {
                usuarioService.asignarRol(idUsuario, idRol);
            }
        }
        return "redirect:/usuarios/asignar-roles/" + idUsuario;
    }

    // QUITAR ROL — redirige de vuelta a la vista de gestión
    @GetMapping("/quitar-rol")
    public String quitarRol(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idRol
    ) {
        usuarioService.quitarRol(idUsuario, idRol);
        return "redirect:/usuarios/asignar-roles/" + idUsuario;
    }
}
