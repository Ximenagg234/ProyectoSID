package edu.icesi.emprendimientos.rest.controller;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.service.PedidoService;
import edu.icesi.emprendimientos.service.UsuarioService;
import edu.icesi.emprendimientos.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;

@Profile("!sid")
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Generación de reportes PDF")
@SecurityRequirement(name = "BearerAuth")
public class RestReporteController {

    private static final Color PRIMARY    = new Color(99,  102, 241);
    private static final Color PRIMARY_BG = new Color(238, 242, 255);
    private static final Color SUCCESS    = new Color(34,  197,  94);
    private static final Color DANGER     = new Color(239,  68,  68);
    private static final Color GRAY_100   = new Color(243, 244, 246);
    private static final Color GRAY_400   = new Color(156, 163, 175);
    private static final Color GRAY_700   = new Color(55,  65,  81);
    private static final Color WHITE      = Color.WHITE;

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public RestReporteController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/metricas/{idUsuario}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Generar reporte PDF de métricas del emprendedor")
    public ResponseEntity<byte[]> generarReporte(@PathVariable Integer idUsuario) throws Exception {

        Usuario usuario = usuarioService.buscarPorId(idUsuario);
        List<Pedido> pedidos = pedidoService.listarRecibidosPorEmprendedor(idUsuario);

        byte[] pdf = buildPdf(usuario, pedidos);

        String filename = "reporte_metricas_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // ─────────────────────────────────────────────────────────────
    // PDF builder
    // ─────────────────────────────────────────────────────────────
    private byte[] buildPdf(Usuario usuario, List<Pedido> pedidos) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 60, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // Page event for header/footer lines
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter w, Document d) {
                PdfContentByte cb = w.getDirectContent();
                // Bottom line
                cb.setColorStroke(GRAY_400);
                cb.setLineWidth(0.5f);
                cb.moveTo(50, 40);
                cb.lineTo(d.getPageSize().getWidth() - 50, 40);
                cb.stroke();
                // Page number
                Font small = FontFactory.getFont(FontFactory.HELVETICA, 8, GRAY_400);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase("Página " + w.getPageNumber() + "  ·  Emprende ICESI", small),
                        d.getPageSize().getWidth() / 2, 28, 0);
            }
        });

        doc.open();

        // ── Fonts ──────────────────────────────────────────────
        Font fTitle   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   22, PRIMARY);
        Font fSub     = FontFactory.getFont(FontFactory.HELVETICA,        11, GRAY_400);
        Font fSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   13, GRAY_700);
        Font fBody    = FontFactory.getFont(FontFactory.HELVETICA,        10, GRAY_700);
        Font fBold    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   10, GRAY_700);
        Font fWhite   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    9, WHITE);
        Font fGreen   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,   10, SUCCESS);
        Font fMuted   = FontFactory.getFont(FontFactory.HELVETICA,         9, GRAY_400);

        NumberFormat cop = NumberFormat.getInstance(new Locale("es", "CO"));

        // ── Header ─────────────────────────────────────────────
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{3f, 1.5f});
        header.setSpacingAfter(24);

        // Left: title block
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(0);
        leftCell.addElement(new Phrase("Reporte de Métricas", fTitle));
        leftCell.addElement(new Phrase("Emprende ICESI  ·  " + usuario.getNombreCompleto(), fSub));
        header.addCell(leftCell);

        // Right: date badge
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(0);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        String dateStr = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es")).format(new Date());
        rightCell.addElement(new Phrase(dateStr, fMuted));
        rightCell.addElement(new Phrase(usuario.getCorreoInstitucional(), fMuted));
        header.addCell(rightCell);

        doc.add(header);

        // Separator line
        addHRule(doc, PRIMARY, 2f);
        doc.add(Chunk.NEWLINE);

        // ── Derived metrics ────────────────────────────────────
        long   totalPedidos   = pedidos.size();
        double ingresos       = pedidos.stream().mapToDouble(p -> p.getTotal() == null ? 0 : p.getTotal().doubleValue()).sum();
        long   totalUnidades  = pedidos.stream().flatMap(p -> p.getDetalles() == null ? java.util.stream.Stream.empty() : p.getDetalles().stream())
                .mapToLong(DetallePedido::getCantidad).sum();
        double ticketPromedio = totalPedidos > 0 ? ingresos / totalPedidos : 0;

        Map<String, Long> estadoCounts = pedidos.stream()
                .collect(Collectors.groupingBy(p -> p.getEstado() == null ? "?" : p.getEstado().getNombre(), Collectors.counting()));

        // ── KPI Cards ──────────────────────────────────────────
        doc.add(sectionTitle("📊  Resumen general", fSection));
        doc.add(Chunk.NEWLINE);

        PdfPTable kpiTable = new PdfPTable(4);
        kpiTable.setWidthPercentage(100);
        kpiTable.setSpacingAfter(20);
        addKpiCard(kpiTable, "Total Pedidos",       String.valueOf(totalPedidos),                           fBold, fMuted);
        addKpiCard(kpiTable, "Ingresos Totales",    "$" + cop.format((long) ingresos),                     fBold, fMuted);
        addKpiCard(kpiTable, "Unidades Vendidas",   String.valueOf(totalUnidades),                          fBold, fMuted);
        addKpiCard(kpiTable, "Ticket Promedio",     "$" + cop.format((long) ticketPromedio),                fBold, fMuted);
        doc.add(kpiTable);

        // ── Estado de pedidos ──────────────────────────────────
        doc.add(sectionTitle("📦  Estado de pedidos", fSection));
        doc.add(Chunk.NEWLINE);

        PdfPTable estadoTable = new PdfPTable(3);
        estadoTable.setWidthPercentage(60);
        estadoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        estadoTable.setSpacingAfter(20);

        addTableHeader(estadoTable, fWhite, "Estado", "Cantidad", "Porcentaje");
        for (Map.Entry<String, Long> e : estadoCounts.entrySet()) {
            double pct = totalPedidos > 0 ? (e.getValue() * 100.0 / totalPedidos) : 0;
            estadoTable.addCell(bodyCell(e.getKey(), fBody));
            estadoTable.addCell(bodyCell(String.valueOf(e.getValue()), fBold));
            estadoTable.addCell(bodyCell(String.format("%.1f%%", pct), fBody));
        }
        doc.add(estadoTable);

        // ── Top productos ──────────────────────────────────────
        doc.add(sectionTitle("🏆  Productos más vendidos", fSection));
        doc.add(Chunk.NEWLINE);

        Map<String, long[]> prodMap = new LinkedHashMap<>(); // [cantidad, ingresos_centavos]
        for (Pedido p : pedidos) {
            if (p.getDetalles() == null) continue;
            for (DetallePedido d : p.getDetalles()) {
                String nombre = d.getProducto() == null ? "?" : d.getProducto().getNombre();
                prodMap.computeIfAbsent(nombre, k -> new long[]{0, 0});
                prodMap.get(nombre)[0] += d.getCantidad();
                prodMap.get(nombre)[1] += d.getSubtotal() == null ? 0 : d.getSubtotal().longValue();
            }
        }
        List<Map.Entry<String, long[]>> topProds = prodMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0]))
                .limit(10)
                .collect(Collectors.toList());

        if (topProds.isEmpty()) {
            doc.add(new Phrase("Sin ventas registradas aún.\n", fMuted));
        } else {
            PdfPTable prodTable = new PdfPTable(4);
            prodTable.setWidthPercentage(100);
            prodTable.setWidths(new float[]{0.6f, 3f, 1.2f, 1.5f});
            prodTable.setSpacingAfter(20);

            addTableHeader(prodTable, fWhite, "#", "Producto", "Uds.", "Ingresos");
            for (int i = 0; i < topProds.size(); i++) {
                Map.Entry<String, long[]> e = topProds.get(i);
                prodTable.addCell(bodyCell(String.valueOf(i + 1), fMuted));
                prodTable.addCell(bodyCell(e.getKey(), fBold));
                prodTable.addCell(bodyCell(String.valueOf(e.getValue()[0]), fBody));
                Font incomeFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, SUCCESS);
                prodTable.addCell(bodyCell("$" + cop.format(e.getValue()[1]), incomeFont));
            }
            doc.add(prodTable);
        }

        // ── Detalle de pedidos ────────────────────────────────
        if (!pedidos.isEmpty()) {
            doc.add(sectionTitle("📋  Historial de pedidos", fSection));
            doc.add(Chunk.NEWLINE);

            PdfPTable pedidosTable = new PdfPTable(4);
            pedidosTable.setWidthPercentage(100);
            pedidosTable.setWidths(new float[]{0.8f, 2f, 1.5f, 1.5f});
            pedidosTable.setSpacingAfter(20);

            addTableHeader(pedidosTable, fWhite, "ID", "Comprador", "Estado", "Total");

            boolean alt = false;
            for (Pedido p : pedidos) {
                Color bg = alt ? GRAY_100 : WHITE;
                pedidosTable.addCell(bodyCell("#" + p.getIdPedido(), fMuted, bg));
                pedidosTable.addCell(bodyCell(p.getUsuario() == null ? "-" : p.getUsuario().getNombreCompleto(), fBody, bg));
                pedidosTable.addCell(bodyCell(p.getEstado() == null ? "-" : p.getEstado().getNombre(), fBody, bg));
                String total = p.getTotal() == null ? "-" : "$" + cop.format(p.getTotal().longValue());
                pedidosTable.addCell(bodyCell(total, fBold, bg));
                alt = !alt;
            }
            doc.add(pedidosTable);
        }

        // ── Footer note ────────────────────────────────────────
        addHRule(doc, GRAY_400, 0.5f);
        doc.add(Chunk.NEWLINE);
        doc.add(new Phrase("Reporte generado automáticamente por Emprende ICESI · " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fMuted));

        doc.close();
        return baos.toByteArray();
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private Paragraph sectionTitle(String text, Font f) {
        Paragraph p = new Paragraph(text, f);
        p.setSpacingBefore(6);
        p.setSpacingAfter(2);
        return p;
    }

    private void addHRule(Document doc, Color color, float width) throws DocumentException {
        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        line.setSpacingAfter(8);
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidthBottom(width);
        cell.setBorderColorBottom(color);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setPaddingBottom(4);
        line.addCell(cell);
        doc.add(line);
    }

    private void addKpiCard(PdfPTable table, String label, String value, Font valFont, Font lblFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(PRIMARY_BG);
        cell.setBorderColor(new Color(199, 210, 254));
        cell.setBorderWidth(1f);
        cell.setPadding(12);


        Paragraph val = new Paragraph(value, valFont);
        val.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(val);

        Paragraph lbl = new Paragraph(label, lblFont);
        lbl.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(lbl);

        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, Font font, String... cols) {
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, font));
            cell.setBackgroundColor(PRIMARY);
            cell.setPadding(8);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }
    }

    private PdfPCell bodyCell(String text, Font font) {
        return bodyCell(text, font, WHITE);
    }

    private PdfPCell bodyCell(String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(7);
        cell.setBorderColor(new Color(229, 231, 235));
        cell.setBorderWidth(0.5f);
        return cell;
    }
}
