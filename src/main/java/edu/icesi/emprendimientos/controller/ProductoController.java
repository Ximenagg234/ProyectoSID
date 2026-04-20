package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.service.CategoriaService;
import edu.icesi.emprendimientos.service.EstadoService;
import edu.icesi.emprendimientos.service.ProductoService;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final EstadoService estadoService;
    private final EmprendimientoService emprendimientoService;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              EstadoService estadoService,
                              EmprendimientoService emprendimientoService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.estadoService = estadoService;
        this.emprendimientoService = emprendimientoService;
    }

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("contenido", "productos/list :: contenido");
        return "layout";
    }

    // FORM CREAR
    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("emprendimientos", emprendimientoService.listar());
        model.addAttribute("contenido", "productos/form :: contenido");
        return "layout";
    }

    // GUARDAR
    @PostMapping
    public String guardar(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return "redirect:/productos";
    }
}
