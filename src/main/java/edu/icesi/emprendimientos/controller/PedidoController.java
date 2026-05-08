package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProductoService productoService;
    private final EstadoService estadoService;
    private final UsuarioService usuarioService;
    private final CalificacionService calificacionService;

    public PedidoController(PedidoService pedidoService,
                            ProductoService productoService,
                            EstadoService estadoService,
                            UsuarioService usuarioService,
                            CalificacionService calificacionService) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.estadoService = estadoService;
        this.usuarioService = usuarioService;
        this.calificacionService = calificacionService;
    }

    // ── MIS PEDIDOS (COMPRADOR) ──────────────────────────────────────────
    @GetMapping("/mis-pedidos")
    public String misPedidos(Authentication auth, Model model) {
        Usuario comprador = usuarioService.buscarPorCorreo(auth.getName());
        List<Pedido> pedidos = pedidoService.listarPorUsuario(comprador.getIdUsuario());

        // Mapa idPedido → yaCalifico para controlar el botón en la vista
        Map<Integer, Boolean> calificados = pedidos.stream()
                .collect(Collectors.toMap(
                        Pedido::getIdPedido,
                        p -> calificacionService.yaCalifico(p.getIdPedido())
                ));

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("calificados", calificados);
        model.addAttribute("contenido", "pedidos/mis-pedidos :: contenido");
        return "layout";
    }

    // ── PEDIDOS RECIBIDOS (EMPRENDEDOR) ──────────────────────────────────
    @GetMapping("/recibidos")
    public String pedidosRecibidos(Authentication auth, Model model) {
        Usuario emprendedor = usuarioService.buscarPorCorreo(auth.getName());
        List<Pedido> recibidos = pedidoService.listarRecibidosPorEmprendedor(emprendedor.getIdUsuario());
        model.addAttribute("pedidos", recibidos);
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("contenido", "pedidos/recibidos :: contenido");
        return "layout";
    }

    // ── CREAR PEDIDO DIRECTO (desde marketplace) ─────────────────────────
    @PostMapping("/crear")
    public String crear(@RequestParam Integer idProducto,
                        @RequestParam(defaultValue = "1") Integer cantidad,
                        Authentication auth,
                        RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.buscarPorId(idProducto);

            if (producto.getStockDisponible() < cantidad) {
                redirectAttributes.addFlashAttribute("error", "Stock insuficiente para ese producto.");
                return "redirect:/marketplace";
            }

            Usuario comprador = usuarioService.buscarPorCorreo(auth.getName());
            Estado pendiente = estadoService.buscarPorNombre("PENDIENTE");

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));

            DetallePedido detalle = new DetallePedido();
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            detalle.setProducto(producto);

            Pedido pedido = new Pedido();
            pedido.setUsuario(comprador);
            pedido.setEmprendimiento(producto.getEmprendimiento());
            pedido.setEstado(pendiente);
            pedido.setFechaPedido(new Date());
            pedido.setDetalles(new ArrayList<>(List.of(detalle)));
            detalle.setPedido(pedido);

            pedidoService.crearPedido(pedido);

            // Decrementar stock del producto
            producto.setStockDisponible(producto.getStockDisponible() - cantidad);
            productoService.actualizar(producto.getIdProducto(), producto);

            redirectAttributes.addFlashAttribute("exito", "¡Pedido realizado con éxito!");
            return "redirect:/pedidos/mis-pedidos";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/marketplace";
        }
    }

    // ── ACTUALIZAR ESTADO (EMPRENDEDOR) ──────────────────────────────────
    @PostMapping("/actualizar-estado/{id}")
    public String actualizarEstado(@PathVariable Integer id,
                                   @RequestParam Integer idEstado,
                                   Authentication auth) {
        Pedido pedido = pedidoService.buscarPorId(id);
        // Solo el dueño del emprendimiento o ADMIN puede cambiar el estado
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!esAdmin && !pedido.getEmprendimiento().getUsuario()
                .getCorreoInstitucional().equals(auth.getName())) {
            return "redirect:/acceso-denegado";
        }
        pedidoService.actualizarEstado(id, idEstado);
        return "redirect:/pedidos/recibidos";
    }
}
