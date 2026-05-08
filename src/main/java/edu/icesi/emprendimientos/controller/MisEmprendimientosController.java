package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/mis-emprendimientos")
public class MisEmprendimientosController {

    private final EmprendimientoService emprendimientoService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final EstadoService estadoService;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;

    public MisEmprendimientosController(EmprendimientoService emprendimientoService,
                                        ProductoService productoService,
                                        CategoriaService categoriaService,
                                        EstadoService estadoService,
                                        UsuarioRepository usuarioRepository,
                                        EstadoRepository estadoRepository) {
        this.emprendimientoService = emprendimientoService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.estadoService = estadoService;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
    }

    // LISTAR MIS EMPRENDIMIENTOS
    @GetMapping
    public String listar(Authentication auth, Model model) {
        Usuario usuario = getUsuario(auth);
        model.addAttribute("emprendimientos",
                emprendimientoService.listarPorUsuario(usuario.getIdUsuario()));
        model.addAttribute("usuario", usuario);
        model.addAttribute("contenido", "mis-emprendimientos/list :: contenido");
        return "layout";
    }

    // FORM NUEVO EMPRENDIMIENTO
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("emprendimiento", new Emprendimiento());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("contenido", "mis-emprendimientos/form :: contenido");
        return "layout";
    }

    // GUARDAR EMPRENDIMIENTO
    @PostMapping
    public String guardar(@ModelAttribute Emprendimiento emprendimiento,
                          Authentication auth, RedirectAttributes ra) {
        Usuario usuario = getUsuario(auth);
        emprendimiento.setUsuario(usuario);
        Estado activo = estadoRepository.findByNombre("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));
        emprendimiento.setEstado(activo);
        emprendimientoService.guardar(emprendimiento);
        ra.addFlashAttribute("exito", "Emprendimiento creado correctamente");
        return "redirect:/mis-emprendimientos";
    }

    // EDITAR EMPRENDIMIENTO
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("emprendimiento", emprendimientoService.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("contenido", "mis-emprendimientos/form :: contenido");
        return "layout";
    }

    // ACTUALIZAR EMPRENDIMIENTO
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Emprendimiento emprendimiento,
                             RedirectAttributes ra) {
        emprendimientoService.actualizar(id, emprendimiento);
        ra.addFlashAttribute("exito", "Emprendimiento actualizado");
        return "redirect:/mis-emprendimientos";
    }

    // ELIMINAR EMPRENDIMIENTO
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes ra) {
        emprendimientoService.eliminar(id);
        ra.addFlashAttribute("exito", "Emprendimiento eliminado");
        return "redirect:/mis-emprendimientos";
    }

    // GESTIONAR PRODUCTOS DEL EMPRENDIMIENTO
    @GetMapping("/{id}/productos")
    public String productos(@PathVariable Integer id, Model model) {
        Emprendimiento emp = emprendimientoService.buscarPorId(id);
        model.addAttribute("emprendimiento", emp);
        model.addAttribute("productos", productoService.listarPorEmprendimiento(id));
        model.addAttribute("contenido", "mis-emprendimientos/productos :: contenido");
        return "layout";
    }

    // FORM NUEVO PRODUCTO
    @GetMapping("/{id}/productos/nuevo")
    public String nuevoProductoForm(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("emprendimiento", emprendimientoService.buscarPorId(id));
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("idEmprendimiento", id);
        model.addAttribute("contenido", "mis-emprendimientos/producto-form :: contenido");
        return "layout";
    }

    // GUARDAR PRODUCTO
    @PostMapping("/{id}/productos")
    public String guardarProducto(@PathVariable Integer id,
                                  @ModelAttribute Producto producto,
                                  RedirectAttributes ra) {
        Emprendimiento emp = emprendimientoService.buscarPorId(id);
        producto.setEmprendimiento(emp);
        Estado activo = estadoRepository.findByNombre("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));
        producto.setEstado(activo);
        productoService.guardar(producto);
        ra.addFlashAttribute("exito", "Producto agregado correctamente");
        return "redirect:/mis-emprendimientos/" + id + "/productos";
    }

    // EDITAR PRODUCTO
    @GetMapping("/{idEmp}/productos/editar/{idProd}")
    public String editarProductoForm(@PathVariable Integer idEmp,
                                     @PathVariable Integer idProd,
                                     Model model) {
        model.addAttribute("producto", productoService.buscarPorId(idProd));
        model.addAttribute("emprendimiento", emprendimientoService.buscarPorId(idEmp));
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("idEmprendimiento", idEmp);
        model.addAttribute("contenido", "mis-emprendimientos/producto-form :: contenido");
        return "layout";
    }

    // ACTUALIZAR PRODUCTO
    @PostMapping("/{idEmp}/productos/actualizar/{idProd}")
    public String actualizarProducto(@PathVariable Integer idEmp,
                                     @PathVariable Integer idProd,
                                     @ModelAttribute Producto producto,
                                     RedirectAttributes ra) {
        productoService.actualizar(idProd, producto);
        ra.addFlashAttribute("exito", "Producto actualizado");
        return "redirect:/mis-emprendimientos/" + idEmp + "/productos";
    }

    // ELIMINAR PRODUCTO
    @GetMapping("/{idEmp}/productos/eliminar/{idProd}")
    public String eliminarProducto(@PathVariable Integer idEmp,
                                   @PathVariable Integer idProd,
                                   RedirectAttributes ra) {
        productoService.eliminar(idProd);
        ra.addFlashAttribute("exito", "Producto eliminado");
        return "redirect:/mis-emprendimientos/" + idEmp + "/productos";
    }

    private Usuario getUsuario(Authentication auth) {
        return usuarioRepository.findByCorreoInstitucional(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
