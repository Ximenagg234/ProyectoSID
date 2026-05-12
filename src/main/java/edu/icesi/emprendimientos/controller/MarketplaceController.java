package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Calificacion;
import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.service.CalificacionService;
import edu.icesi.emprendimientos.service.CategoriaService;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import edu.icesi.emprendimientos.service.PedidoService;
import edu.icesi.emprendimientos.service.ProductoService;
import edu.icesi.emprendimientos.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceController {

    private final ProductoService productoService;
    private final EmprendimientoService emprendimientoService;
    private final CategoriaService categoriaService;
    private final CalificacionService calificacionService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;

    public MarketplaceController(ProductoService productoService,
                                 EmprendimientoService emprendimientoService,
                                 CategoriaService categoriaService,
                                 CalificacionService calificacionService,
                                 UsuarioService usuarioService,
                                 PedidoService pedidoService) {
        this.productoService = productoService;
        this.emprendimientoService = emprendimientoService;
        this.categoriaService = categoriaService;
        this.calificacionService = calificacionService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
    }

    // CATÁLOGO PRINCIPAL
    @GetMapping
    public String marketplace(
            @RequestParam(required = false) Integer categoria,
            Model model) {

        List<Producto> productos;

        if (categoria != null) {
            productos = productoService.listarActivosPorCategoria(categoria);
        } else {
            productos = productoService.listarActivos();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("destacados", emprendimientoService.listarDestacados());
        model.addAttribute("contenido", "marketplace/list :: contenido");
        return "layout";
    }

    // DETALLE DE EMPRENDIMIENTO
    @GetMapping("/emprendimiento/{id}")
    public String detalleEmprendimiento(@PathVariable Integer id, Model model, Authentication auth) {

        Emprendimiento emprendimiento = emprendimientoService.buscarPorId(id);
        List<Producto> productos = productoService.listarPorEmprendimiento(id);

        model.addAttribute("emprendimiento", emprendimiento);
        model.addAttribute("productos", productos);
        model.addAttribute("calificaciones", calificacionService.listarPorEmprendimiento(id));
        model.addAttribute("promedio", calificacionService.promedioPorEmprendimiento(id));

        // Pedidos ENTREGADO del usuario actual para este emprendimiento (aún sin reseña)
        if (auth != null) {
            try {
                Usuario usuario = usuarioService.buscarPorCorreo(auth.getName());
                List<Pedido> pedidosParaCalificar = pedidoService.listarPorUsuario(usuario.getIdUsuario())
                        .stream()
                        .filter(p -> p.getEstado() != null && "ENTREGADO".equals(p.getEstado().getNombre()))
                        .filter(p -> p.getEmprendimiento() != null &&
                                p.getEmprendimiento().getIdEmprendimiento().equals(id))
                        .filter(p -> !calificacionService.yaCalifico(p.getIdPedido()))
                        .collect(Collectors.toList());
                model.addAttribute("pedidosParaCalificar", pedidosParaCalificar);
            } catch (Exception ignored) {}
        }

        model.addAttribute("contenido", "marketplace/emprendimiento :: contenido");
        return "layout";
    }

    // GUARDAR RESEÑA
    @PostMapping("/calificar")
    public String calificar(@RequestParam Integer idEmprendimiento,
                            @RequestParam Integer idPedido,
                            @RequestParam Integer puntuacion,
                            @RequestParam(required = false) String comentario,
                            Authentication auth,
                            RedirectAttributes ra) {
        try {
            Usuario usuario = usuarioService.buscarPorCorreo(auth.getName());
            Emprendimiento emprendimiento = emprendimientoService.buscarPorId(idEmprendimiento);
            Pedido pedido = pedidoService.buscarPorId(idPedido);

            Calificacion cal = new Calificacion();
            cal.setPuntuacion(puntuacion);
            cal.setComentario(comentario);
            cal.setFecha(new Date());
            cal.setUsuario(usuario);
            cal.setEmprendimiento(emprendimiento);
            cal.setPedido(pedido);

            calificacionService.guardar(cal);
            ra.addFlashAttribute("exito", "¡Reseña enviada con éxito!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo guardar la reseña.");
        }
        return "redirect:/marketplace/emprendimiento/" + idEmprendimiento;
    }

    // RANKING DE EMPRENDIMIENTOS
    @GetMapping("/ranking")
    public String ranking(Model model) {
        List<Emprendimiento> todos = emprendimientoService.listar();

        List<Object[]> ranking = todos.stream()
                .map(e -> new Object[]{e, calificacionService.promedioPorEmprendimiento(e.getIdEmprendimiento())})
                .sorted(Comparator.comparingDouble(o -> -((Double) o[1])))
                .collect(Collectors.toList());

        model.addAttribute("ranking", ranking);
        model.addAttribute("contenido", "marketplace/ranking :: contenido");
        return "layout";
    }
}
