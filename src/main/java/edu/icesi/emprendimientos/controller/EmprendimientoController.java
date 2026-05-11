package edu.icesi.emprendimientos.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.service.CalificacionService;
import edu.icesi.emprendimientos.service.CategoriaService;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import edu.icesi.emprendimientos.service.EstadoService;
import edu.icesi.emprendimientos.service.PedidoService;
import edu.icesi.emprendimientos.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/emprendimientos")
public class EmprendimientoController {

    private final EmprendimientoService emprendimientoService;
    private final CategoriaService categoriaService;
    private final EstadoService estadoService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final CalificacionService calificacionService;

    public EmprendimientoController(EmprendimientoService emprendimientoService,
                                    CategoriaService categoriaService,
                                    EstadoService estadoService,
                                    UsuarioService usuarioService,
                                    PedidoService pedidoService,
                                    CalificacionService calificacionService) {
        this.emprendimientoService = emprendimientoService;
        this.categoriaService = categoriaService;
        this.estadoService = estadoService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.calificacionService = calificacionService;
    }

    // ── DASHBOARD DEL EMPRENDEDOR ────────────────────────────────────────
    @Transactional(readOnly = true)
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Usuario actual = usuarioService.buscarPorCorreo(auth.getName());
        List<Emprendimiento> misEmps = emprendimientoService.listarPorUsuario(actual.getIdUsuario());
        List<Pedido> pedidosRecibidos = pedidoService.listarRecibidosPorEmprendedor(actual.getIdUsuario());

        BigDecimal ingresos = pedidosRecibidos.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pedidosPendientes = pedidosRecibidos.stream()
                .filter(p -> "PENDIENTE".equals(p.getEstado() != null ? p.getEstado().getNombre() : ""))
                .count();

        // Top productos por cantidad vendida
        Map<String, Long> topProductos = pedidosRecibidos.stream()
                .flatMap(p -> p.getDetalles() != null ? p.getDetalles().stream() : java.util.stream.Stream.empty())
                .collect(Collectors.groupingBy(
                        d -> d.getProducto() != null ? d.getProducto().getNombre() : "Desconocido",
                        Collectors.summingLong(d -> d.getCantidad() != null ? d.getCantidad() : 0L)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, java.util.LinkedHashMap::new));

        // Distribución de pedidos por estado (para gráfico doughnut)
        Map<String, Long> pedidosPorEstado = pedidosRecibidos.stream()
                .filter(p -> p.getEstado() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getEstado().getNombre(),
                        Collectors.counting()
                ));

        model.addAttribute("misEmps", misEmps);
        model.addAttribute("totalPedidos", pedidosRecibidos.size());
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("ingresos", ingresos);
        model.addAttribute("topProductos", topProductos);
        model.addAttribute("pedidosPorEstado", pedidosPorEstado);
        model.addAttribute("contenido", "emprendimientos/dashboard :: contenido");
        return "layout";
    }

    // ── MIS EMPRENDIMIENTOS (EMPRENDEDOR ve solo los suyos) ──────────────
    @GetMapping("/mis-emprendimientos")
    public String misEmprendimientos(Authentication auth, Model model) {
        Usuario actual = usuarioService.buscarPorCorreo(auth.getName());
        model.addAttribute("emprendimientos", emprendimientoService.listarPorUsuario(actual.getIdUsuario()));
        model.addAttribute("contenido", "emprendimientos/list :: contenido");
        return "layout";
    }

    // ── LISTADO ADMIN (todos) ────────────────────────────────────────────
    @GetMapping
    public String listarTodos(Model model) {
        model.addAttribute("emprendimientos", emprendimientoService.listar());
        model.addAttribute("contenido", "emprendimientos/list :: contenido");
        return "layout";
    }

    // ── FORM CREAR ───────────────────────────────────────────────────────
    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("emprendimiento", new Emprendimiento());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("contenido", "emprendimientos/form :: contenido");
        return "layout";
    }

    // ── GUARDAR ──────────────────────────────────────────────────────────
    @PostMapping
    public String guardar(@ModelAttribute Emprendimiento emprendimiento,
                          Authentication auth,
                          RedirectAttributes redirectAttributes) {
        try {
            // El propietario es siempre el usuario autenticado
            Usuario propietario = usuarioService.buscarPorCorreo(auth.getName());
            emprendimiento.setUsuario(propietario);
            emprendimientoService.guardar(emprendimiento);
            return "redirect:/emprendimientos/mis-emprendimientos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/emprendimientos/nuevo";
        }
    }

    // ── FORM EDITAR ──────────────────────────────────────────────────────
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Authentication auth, Model model) {
        if (!tieneAcceso(id, auth)) return "redirect:/acceso-denegado";
        model.addAttribute("emprendimiento", emprendimientoService.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("estados", estadoService.listar());
        model.addAttribute("contenido", "emprendimientos/form :: contenido");
        return "layout";
    }

    // ── ACTUALIZAR ───────────────────────────────────────────────────────
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Emprendimiento emprendimiento,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        if (!tieneAcceso(id, auth)) return "redirect:/acceso-denegado";
        try {
            emprendimientoService.actualizar(id, emprendimiento);
            return "redirect:/emprendimientos/mis-emprendimientos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/emprendimientos/editar/" + id;
        }
    }

    // ── ELIMINAR ─────────────────────────────────────────────────────────
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth) {
        if (!tieneAcceso(id, auth)) return "redirect:/acceso-denegado";
        emprendimientoService.eliminar(id);
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return esAdmin ? "redirect:/emprendimientos" : "redirect:/emprendimientos/mis-emprendimientos";
    }

    // ── TOGGLE DESTACADO (solo ADMIN) ────────────────────────────────────
    @GetMapping("/destacar/{id}")
    public String toggleDestacado(@PathVariable Integer id) {
        emprendimientoService.toggleDestacado(id);
        return "redirect:/emprendimientos";
    }

    // ── REPORTE PDF ──────────────────────────────────────────────────────
    @GetMapping("/reporte")
    public void reportePdf(Authentication auth, HttpServletResponse response) throws IOException {
        Usuario actual = usuarioService.buscarPorCorreo(auth.getName());
        List<Emprendimiento> misEmps = emprendimientoService.listarPorUsuario(actual.getIdUsuario());
        List<Pedido> pedidos = pedidoService.listarRecibidosPorEmprendedor(actual.getIdUsuario());

        BigDecimal totalIngresos = pedidos.stream().map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> topProductos = pedidos.stream()
                .flatMap(p -> p.getDetalles() != null ? p.getDetalles().stream() : java.util.stream.Stream.empty())
                .collect(Collectors.groupingBy(
                        d -> d.getProducto() != null ? d.getProducto().getNombre() : "Desconocido",
                        Collectors.summingLong(d -> d.getCantidad() != null ? d.getCantidad() : 0L)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, java.util.LinkedHashMap::new));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte-emprendimientos-" +
                new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf\"");

        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // Fuentes
        Font fTitle  = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(99, 102, 241));
        Font fSub    = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(17, 24, 39));
        Font fNormal = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(107, 114, 128));
        Font fBold   = new Font(Font.HELVETICA, 10, Font.BOLD,   new Color(17, 24, 39));
        Font fHead   = new Font(Font.HELVETICA, 9,  Font.BOLD,   Color.WHITE);

        Color primary    = new Color(99, 102, 241);
        Color lightGray  = new Color(249, 250, 251);
        Color borderGray = new Color(229, 231, 235);

        // Encabezado
        Paragraph title = new Paragraph("IcesiEmprende — Reporte de Ventas", fTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph fecha = new Paragraph(
                "Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) +
                "   |   Emprendedor: " + actual.getNombreCompleto(), fNormal);
        fecha.setAlignment(Element.ALIGN_CENTER);
        fecha.setSpacingAfter(20);
        doc.add(fecha);

        // Resumen métricas
        doc.add(new Paragraph("Resumen General", fSub) {{ setSpacingAfter(8); }});

        PdfPTable metrics = new PdfPTable(3);
        metrics.setWidthPercentage(100);
        metrics.setSpacingAfter(20);
        String[] mLabels = {"Total Pedidos", "Ingresos Totales (COP)", "Emprendimientos"};
        String[] mValues = {
            String.valueOf(pedidos.size()),
            String.format("%,.0f", totalIngresos),
            String.valueOf(misEmps.size())
        };
        for (int i = 0; i < 3; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(lightGray);
            cell.setPadding(12);
            cell.setBorderColor(borderGray);
            Paragraph lbl = new Paragraph(mLabels[i], fNormal);
            Paragraph val = new Paragraph(mValues[i], new Font(Font.HELVETICA, 16, Font.BOLD, primary));
            cell.addElement(lbl);
            cell.addElement(val);
            metrics.addCell(cell);
        }
        doc.add(metrics);

        // Top productos
        if (!topProductos.isEmpty()) {
            doc.add(new Paragraph("Top 5 Productos Más Vendidos", fSub) {{ setSpacingAfter(8); }});
            PdfPTable tbl = new PdfPTable(2);
            tbl.setWidthPercentage(70);
            tbl.setHorizontalAlignment(Element.ALIGN_LEFT);
            tbl.setSpacingAfter(20);
            for (String h : new String[]{"Producto", "Unidades"}) {
                PdfPCell hc = new PdfPCell(new Phrase(h, fHead));
                hc.setBackgroundColor(primary);
                hc.setPadding(8);
                hc.setBorder(Rectangle.NO_BORDER);
                tbl.addCell(hc);
            }
            boolean alt = false;
            for (Map.Entry<String, Long> e : topProductos.entrySet()) {
                Color bg = alt ? lightGray : Color.WHITE;
                PdfPCell n = new PdfPCell(new Phrase(e.getKey(), fBold));
                n.setBackgroundColor(bg); n.setPadding(7); n.setBorderColor(borderGray);
                PdfPCell v = new PdfPCell(new Phrase(e.getValue() + " uds", fNormal));
                v.setBackgroundColor(bg); v.setPadding(7); v.setBorderColor(borderGray);
                tbl.addCell(n); tbl.addCell(v);
                alt = !alt;
            }
            doc.add(tbl);
        }

        // Tabla de pedidos
        if (!pedidos.isEmpty()) {
            doc.add(new Paragraph("Detalle de Pedidos", fSub) {{ setSpacingAfter(8); }});
            PdfPTable pt = new PdfPTable(5);
            pt.setWidthPercentage(100);
            pt.setWidths(new float[]{1f, 2.5f, 2f, 1.5f, 1.5f});
            for (String h : new String[]{"#", "Emprendimiento", "Estado", "Total (COP)", "Fecha"}) {
                PdfPCell hc = new PdfPCell(new Phrase(h, fHead));
                hc.setBackgroundColor(primary); hc.setPadding(7); hc.setBorder(Rectangle.NO_BORDER);
                pt.addCell(hc);
            }
            boolean alt = false;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Pedido p : pedidos) {
                Color bg = alt ? lightGray : Color.WHITE;
                addCell(pt, "#" + p.getIdPedido(), fBold,   bg, borderGray);
                addCell(pt, p.getEmprendimiento() != null ? p.getEmprendimiento().getNombre() : "-", fNormal, bg, borderGray);
                addCell(pt, p.getEstado() != null ? p.getEstado().getNombre() : "-", fNormal, bg, borderGray);
                addCell(pt, String.format("%,.0f", p.getTotal()), fNormal, bg, borderGray);
                addCell(pt, p.getFechaPedido() != null ? sdf.format(p.getFechaPedido()) : "-", fNormal, bg, borderGray);
                alt = !alt;
            }
            doc.add(pt);
        }

        doc.close();
    }

    private void addCell(PdfPTable t, String text, Font f, Color bg, Color border) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBackgroundColor(bg); c.setPadding(6); c.setBorderColor(border);
        t.addCell(c);
    }

    // ── HELPER: ownership check ──────────────────────────────────────────
    private boolean tieneAcceso(Integer idEmprendimiento, Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (esAdmin) return true;
        Emprendimiento e = emprendimientoService.buscarPorId(idEmprendimiento);
        return e.getUsuario().getCorreoInstitucional().equals(auth.getName());
    }
}
