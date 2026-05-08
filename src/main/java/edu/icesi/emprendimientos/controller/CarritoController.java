package edu.icesi.emprendimientos.controller;

import edu.icesi.emprendimientos.carrito.ItemCarrito;
import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.PedidoService;
import edu.icesi.emprendimientos.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private static final String SESSION_KEY = "carrito";

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;
    private final EstadoRepository estadoRepository;

    public CarritoController(ProductoService productoService,
                             PedidoService pedidoService,
                             UsuarioRepository usuarioRepository,
                             EstadoRepository estadoRepository) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.usuarioRepository = usuarioRepository;
        this.estadoRepository = estadoRepository;
    }

    // ── VER CARRITO ──────────────────────────────────────────
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Map<Integer, ItemCarrito> carrito = obtenerCarrito(session);
        BigDecimal total = carrito.values().stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("items", carrito.values());
        model.addAttribute("total", total);
        model.addAttribute("contenido", "carrito/index :: contenido");
        return "layout";
    }

    // ── AGREGAR PRODUCTO ─────────────────────────────────────
    @PostMapping("/agregar")
    public String agregar(@RequestParam Integer idProducto,
                          @RequestParam(defaultValue = "1") Integer cantidad,
                          HttpSession session,
                          RedirectAttributes ra) {

        Producto producto = productoService.buscarPorId(idProducto);

        if (producto.getStockDisponible() < cantidad) {
            ra.addFlashAttribute("errorCarrito", "Stock insuficiente para " + producto.getNombre());
            return "redirect:/marketplace";
        }

        Map<Integer, ItemCarrito> carrito = obtenerCarrito(session);

        if (carrito.containsKey(idProducto)) {
            // Ya existe — sumar cantidad
            ItemCarrito item = carrito.get(idProducto);
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (nuevaCantidad > producto.getStockDisponible()) {
                ra.addFlashAttribute("errorCarrito", "No hay suficiente stock disponible");
                return "redirect:/marketplace";
            }
            item.setCantidad(nuevaCantidad);
        } else {
            // Nuevo item
            carrito.put(idProducto, new ItemCarrito(
                    idProducto,
                    producto.getNombre(),
                    producto.getEmprendimiento().getNombre(),
                    producto.getEmprendimiento().getIdEmprendimiento(),
                    producto.getPrecio(),
                    cantidad
            ));
        }

        session.setAttribute(SESSION_KEY, carrito);
        ra.addFlashAttribute("exitoCarrito", "¡" + producto.getNombre() + " agregado al carrito!");
        return "redirect:/marketplace";
    }

    // ── QUITAR PRODUCTO ──────────────────────────────────────
    @PostMapping("/quitar")
    public String quitar(@RequestParam Integer idProducto, HttpSession session) {
        Map<Integer, ItemCarrito> carrito = obtenerCarrito(session);
        carrito.remove(idProducto);
        session.setAttribute(SESSION_KEY, carrito);
        return "redirect:/carrito";
    }

    // ── ACTUALIZAR CANTIDAD ──────────────────────────────────
    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Integer idProducto,
                             @RequestParam Integer cantidad,
                             HttpSession session) {
        Map<Integer, ItemCarrito> carrito = obtenerCarrito(session);
        if (carrito.containsKey(idProducto)) {
            if (cantidad <= 0) {
                carrito.remove(idProducto);
            } else {
                carrito.get(idProducto).setCantidad(cantidad);
            }
        }
        session.setAttribute(SESSION_KEY, carrito);
        return "redirect:/carrito";
    }

    // ── VACIAR CARRITO ───────────────────────────────────────
    @PostMapping("/vaciar")
    public String vaciar(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/carrito";
    }

    // ── CONFIRMAR PEDIDO ─────────────────────────────────────
    @PostMapping("/confirmar")
    public String confirmar(HttpSession session,
                            Authentication auth,
                            RedirectAttributes ra) {

        Map<Integer, ItemCarrito> carrito = obtenerCarrito(session);

        if (carrito.isEmpty()) {
            ra.addFlashAttribute("errorCarrito", "Tu carrito está vacío");
            return "redirect:/carrito";
        }

        // Obtener usuario autenticado
        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Estado PENDIENTE
        Estado estadoPendiente = estadoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no configurado"));

        // Agrupar items por emprendimiento y crear un pedido por cada uno
        Map<Integer, List<ItemCarrito>> porEmprendimiento = new HashMap<>();
        for (ItemCarrito item : carrito.values()) {
            porEmprendimiento
                    .computeIfAbsent(item.getIdEmprendimiento(), k -> new ArrayList<>())
                    .add(item);
        }

        List<Integer> pedidosCreados = new ArrayList<>();

        for (Map.Entry<Integer, List<ItemCarrito>> entry : porEmprendimiento.entrySet()) {
            Integer idEmprendimiento = entry.getKey();
            List<ItemCarrito> items = entry.getValue();

            // Construir el pedido
            Pedido pedido = new Pedido();
            pedido.setFechaPedido(new Date());
            pedido.setUsuario(usuario);
            pedido.setEstado(estadoPendiente);

            // Emprendimiento
            Emprendimiento emp = new Emprendimiento();
            emp.setIdEmprendimiento(idEmprendimiento);
            pedido.setEmprendimiento(emp);

            // Detalles
            List<DetallePedido> detalles = new ArrayList<>();
            for (ItemCarrito item : items) {
                Producto prod = new Producto();
                prod.setIdProducto(item.getIdProducto());

                DetallePedido detalle = new DetallePedido();
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecioUnitario());
                detalle.setSubtotal(item.getSubtotal());
                detalle.setProducto(prod);
                detalles.add(detalle);
            }

            pedido.setDetalles(detalles);
            Pedido guardado = pedidoService.crearPedido(pedido);
            pedidosCreados.add(guardado.getIdPedido());
        }

        // Vaciar carrito
        session.removeAttribute(SESSION_KEY);

        ra.addFlashAttribute("pedidosConfirmados", pedidosCreados.size());
        return "redirect:/pedidos/mis-pedidos";
    }

    // ── HELPER ───────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Map<Integer, ItemCarrito> obtenerCarrito(HttpSession session) {
        Map<Integer, ItemCarrito> carrito =
                (Map<Integer, ItemCarrito>) session.getAttribute(SESSION_KEY);
        if (carrito == null) {
            carrito = new LinkedHashMap<>();
        }
        return carrito;
    }
}
