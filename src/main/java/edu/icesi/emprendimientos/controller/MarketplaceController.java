package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.service.CategoriaService;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import edu.icesi.emprendimientos.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceController {

    private final ProductoService productoService;
    private final EmprendimientoService emprendimientoService;
    private final CategoriaService categoriaService;

    public MarketplaceController(ProductoService productoService,
                                 EmprendimientoService emprendimientoService,
                                 CategoriaService categoriaService) {
        this.productoService = productoService;
        this.emprendimientoService = emprendimientoService;
        this.categoriaService = categoriaService;
    }

    // CATÁLOGO PRINCIPAL
    @GetMapping
    public String marketplace(
            @RequestParam(required = false) Integer categoria,
            Model model) {

        List<Producto> productos;

        if (categoria != null) {
            productos = productoService.listarPorCategoria(categoria);
        } else {
            productos = productoService.listar();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("categoriaSeleccionada", categoria);
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
        model.addAttribute("contenido", "marketplace/emprendimiento :: contenido");
        return "layout";
    }
}
