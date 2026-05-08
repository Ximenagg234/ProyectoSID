package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.service.CalificacionService;
import edu.icesi.emprendimientos.service.CategoriaService;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import edu.icesi.emprendimientos.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceController {

    private final ProductoService productoService;
    private final EmprendimientoService emprendimientoService;
    private final CategoriaService categoriaService;
    private final CalificacionService calificacionService;

    public MarketplaceController(ProductoService productoService,
                                 EmprendimientoService emprendimientoService,
                                 CategoriaService categoriaService,
                                 CalificacionService calificacionService) {
        this.productoService = productoService;
        this.emprendimientoService = emprendimientoService;
        this.categoriaService = categoriaService;
        this.calificacionService = calificacionService;
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
    public String detalleEmprendimiento(@PathVariable Integer id, Model model) {

        Emprendimiento emprendimiento = emprendimientoService.buscarPorId(id);
        List<Producto> productos = productoService.listarPorEmprendimiento(id);

        model.addAttribute("emprendimiento", emprendimiento);
        model.addAttribute("productos", productos);
        model.addAttribute("calificaciones", calificacionService.listarPorEmprendimiento(id));
        model.addAttribute("promedio", calificacionService.promedioPorEmprendimiento(id));
        model.addAttribute("contenido", "marketplace/emprendimiento :: contenido");
        return "layout";
    }

    // RANKING DE EMPRENDIMIENTOS
    @GetMapping("/ranking")
    public String ranking(Model model) {
        List<Emprendimiento> todos = emprendimientoService.listar();

        // Build ranked list: each entry carries emprendimiento + promedio, sorted desc
        List<Object[]> ranking = todos.stream()
                .map(e -> new Object[]{e, calificacionService.promedioPorEmprendimiento(e.getIdEmprendimiento())})
                .sorted(Comparator.comparingDouble(o -> -((Double) o[1])))
                .collect(Collectors.toList());

        model.addAttribute("ranking", ranking);
        model.addAttribute("contenido", "marketplace/ranking :: contenido");
        return "layout";
    }
}
