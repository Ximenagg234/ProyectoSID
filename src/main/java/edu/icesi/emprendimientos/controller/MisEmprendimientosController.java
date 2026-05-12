package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.repository.SemestreRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
    private final SemestreRepository semestreRepository;
    private final FileStorageService fileStorageService;
    private final ImagenProductoService imagenProductoService;

    public MisEmprendimientosController(EmprendimientoService emprendimientoService,
                                        ProductoService productoService,
                                        CategoriaService categoriaService,
                                        EstadoService estadoService,
                                        UsuarioRepository usuarioRepository,
                                        EstadoRepository estadoRepository,
                                        SemestreRepository semestreRepository,
                                        FileStorageService fileStorageService,
                                        ImagenProductoService imagenProductoService) {
        this.emprendimientoService = emprendimientoService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.estadoService = estadoService;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
        this.semestreRepository = semestreRepository;
        this.fileStorageService = fileStorageService;
        this.imagenProductoService = imagenProductoService;
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
                          @RequestParam(value = "logo", required = false) MultipartFile logo,
                          Authentication auth, RedirectAttributes ra) {
        Usuario usuario = getUsuario(auth);
        emprendimiento.setUsuario(usuario);

        Estado activo = estadoRepository.findByNombre("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));
        emprendimiento.setEstado(activo);

        Semestre semestre = semestreRepository
                .findFirstByEstado_NombreOrderByFechaInicioDesc("ACTIVO")
                .orElse(semestreRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No hay semestres disponibles")));
        emprendimiento.setSemestre(semestre);

        if (logo != null && !logo.isEmpty()) {
            try {
                emprendimiento.setLogoUrl(fileStorageService.guardar(logo, "logos"));
            } catch (IOException e) {
                ra.addFlashAttribute("error", "Error al subir el logo: " + e.getMessage());
            }
        }

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
                             @RequestParam(value = "logo", required = false) MultipartFile logo,
                             RedirectAttributes ra) {
        // Preservar logoUrl existente si no se sube uno nuevo
        Emprendimiento existente = emprendimientoService.buscarPorId(id);
        if (logo != null && !logo.isEmpty()) {
            try {
                emprendimiento.setLogoUrl(fileStorageService.guardar(logo, "logos"));
            } catch (IOException e) {
                ra.addFlashAttribute("error", "Error al subir el logo: " + e.getMessage());
                emprendimiento.setLogoUrl(existente.getLogoUrl());
            }
        } else {
            emprendimiento.setLogoUrl(existente.getLogoUrl());
        }

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
                                  @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                  RedirectAttributes ra) {
        Emprendimiento emp = emprendimientoService.buscarPorId(id);
        producto.setEmprendimiento(emp);
        Estado activo = estadoRepository.findByNombre("ACTIVO")
                .orElseThrow(() -> new RuntimeException("Estado ACTIVO no encontrado"));
        producto.setEstado(activo);
        Producto guardado = productoService.guardar(producto);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                String url = fileStorageService.guardar(imagen, "productos");
                ImagenProducto img = new ImagenProducto();
                img.setUrlImagen(url);
                img.setProducto(guardado);
                imagenProductoService.guardar(img);
            } catch (IOException e) {
                ra.addFlashAttribute("error", "Producto guardado pero error al subir imagen: " + e.getMessage());
            }
        }

        ra.addFlashAttribute("exito", "Producto agregado correctamente");
        return "redirect:/mis-emprendimientos/" + id + "/productos";
    }

    // EDITAR PRODUCTO
    @GetMapping("/{idEmp}/productos/editar/{idProd}")
    public String editarProductoForm(@PathVariable Integer idEmp,
                                     @PathVariable Integer idProd,
                                     Model model) {
        Producto producto = productoService.buscarPorId(idProd);
        model.addAttribute("producto", producto);
        model.addAttribute("emprendimiento", emprendimientoService.buscarPorId(idEmp));
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("idEmprendimiento", idEmp);
        // Primera imagen existente para mostrar preview
        List<ImagenProducto> imagenes = imagenProductoService.listarPorProducto(idProd);
        model.addAttribute("imagenActual", imagenes.isEmpty() ? null : imagenes.get(0).getUrlImagen());
        model.addAttribute("contenido", "mis-emprendimientos/producto-form :: contenido");
        return "layout";
    }

    // ACTUALIZAR PRODUCTO
    @PostMapping("/{idEmp}/productos/actualizar/{idProd}")
    public String actualizarProducto(@PathVariable Integer idEmp,
                                     @PathVariable Integer idProd,
                                     @ModelAttribute Producto producto,
                                     @RequestParam(value = "imagen", required = false) MultipartFile imagen,
                                     RedirectAttributes ra) {
        // Cambiar estado automáticamente según el stock
        String nombreEstado = (producto.getStockDisponible() != null && producto.getStockDisponible() > 0)
                ? "ACTIVO" : "INACTIVO";
        Estado estado = estadoRepository.findByNombre(nombreEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        producto.setEstado(estado);

        productoService.actualizar(idProd, producto);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                String url = fileStorageService.guardar(imagen, "productos");
                // Eliminar imágenes anteriores antes de guardar la nueva
                for (ImagenProducto imgVieja : imagenProductoService.listarPorProducto(idProd)) {
                    imagenProductoService.eliminar(imgVieja.getIdImagen());
                }
                Producto prod = productoService.buscarPorId(idProd);
                ImagenProducto img = new ImagenProducto();
                img.setUrlImagen(url);
                img.setProducto(prod);
                imagenProductoService.guardar(img);
            } catch (IOException e) {
                ra.addFlashAttribute("error", "Producto actualizado pero error al subir imagen: " + e.getMessage());
            }
        }

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
