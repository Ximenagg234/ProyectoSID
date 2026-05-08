package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.EmprendimientoRepository;
import edu.icesi.emprendimientos.repository.PedidoRepository;
import edu.icesi.emprendimientos.repository.ProductoRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.ProductoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;
    private final EmprendimientoRepository emprendimientoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoService productoService;

    public HomeController(UsuarioRepository usuarioRepository,
                          EmprendimientoRepository emprendimientoRepository,
                          ProductoRepository productoRepository,
                          PedidoRepository pedidoRepository,
                          ProductoService productoService) {
        this.usuarioRepository = usuarioRepository;
        this.emprendimientoRepository = emprendimientoRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Authentication auth, Model model) {
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correo).orElse(null);
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);

        boolean isAdmin      = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmprendedor= auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPRENDEDOR"));
        boolean isComprador  = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COMPRADOR"));

        model.addAttribute("isAdmin",       isAdmin);
        model.addAttribute("isEmprendedor", isEmprendedor);
        model.addAttribute("isComprador",   isComprador);

        if (isAdmin) {
            model.addAttribute("totalUsuarios",        usuarioRepository.count());
            model.addAttribute("totalEmprendimientos", emprendimientoRepository.count());
            model.addAttribute("totalProductos",       productoRepository.count());
            model.addAttribute("totalPedidos",         pedidoRepository.count());
        }

        if (isEmprendedor) {
            var misEmprendimientos = emprendimientoRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
            model.addAttribute("misEmprendimientos", misEmprendimientos);
            model.addAttribute("pedidosRecibidos",
                    pedidoRepository.findByEmprendimiento_Usuario_IdUsuario(usuario.getIdUsuario()));
        }

        if (isComprador) {
            model.addAttribute("productosDestacados", productoService.listar().stream().limit(4).toList());
            model.addAttribute("misPedidos",
                    pedidoRepository.findByUsuario_IdUsuario(usuario.getIdUsuario()));
        }

        model.addAttribute("contenido", "home/dashboard :: contenido");
        return "layout";
    }
}
