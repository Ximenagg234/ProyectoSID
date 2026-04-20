package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.service.PermissionService;
import edu.icesi.emprendimientos.service.RolService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;
    private final PermissionService permissionService;

    public RolController(RolService rolService, PermissionService permissionService) {
        this.rolService = rolService;
        this.permissionService = permissionService;
    }

    // LISTAR ROLES
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("roles", rolService.listar());
        model.addAttribute("contenido", "roles/list :: contenido");
        return "layout";
    }

    // FORM CREAR
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("rol", new Rol());
        model.addAttribute("contenido", "roles/form :: contenido");
        return "layout";
    }

    // GUARDAR
    @PostMapping
    public String guardar(@ModelAttribute Rol rol) {
        rolService.guardar(rol);
        return "redirect:/roles";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        rolService.eliminar(id);
        return "redirect:/roles";
    }

    // ASIGNAR PERMISOS (FORM)
    @GetMapping("/asignar-permisos/{id}")
    public String asignarPermisos(@PathVariable Integer id, Model model) {
        model.addAttribute("rol", rolService.buscarPorId(id));
        model.addAttribute("permisos", permissionService.listar());
        model.addAttribute("contenido", "roles/asignar-permisos :: contenido");
        return "layout";
    }

    // GUARDAR ASIGNACIÓN
    @PostMapping("/asignar-permisos")
    public String guardarPermisos(
            @RequestParam Integer idRol,
            @RequestParam(required = false) List<Integer> permisos
    ) {
        if (permisos != null) {
            for (Integer idPermission : permisos) {
                rolService.asignarPermiso(idRol, idPermission);
            }
        }
        return "redirect:/roles";
    }
}
