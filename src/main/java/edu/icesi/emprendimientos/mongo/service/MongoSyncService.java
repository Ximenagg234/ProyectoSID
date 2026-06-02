package edu.icesi.emprendimientos.mongo.service;

import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.mongo.document.*;
import edu.icesi.emprendimientos.mongo.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de sincronización entre PostgreSQL y MongoDB.
 *
 * Patrón dual-write: cuando se produce un cambio en PostgreSQL
 * (nuevo usuario, nuevo pedido, nueva calificación, etc.),
 * este servicio replica los datos en MongoDB con el modelo
 * desnormalizado apropiado para cada colección.
 *
 * PostgreSQL = fuente de verdad para usuarios/roles/permisos
 * MongoDB    = fuente de verdad para emprendimientos/pedidos/calificaciones
 */
@Service
public class MongoSyncService {

    private final EmprendimientoMongoRepository emprendimientoRepo;
    private final CalificacionMongoRepository   calificacionRepo;
    private final PedidoMongoRepository         pedidoRepo;
    private final UsuarioSyncMongoRepository    usuarioSyncRepo;

    public MongoSyncService(EmprendimientoMongoRepository emprendimientoRepo,
                            CalificacionMongoRepository calificacionRepo,
                            PedidoMongoRepository pedidoRepo,
                            UsuarioSyncMongoRepository usuarioSyncRepo) {
        this.emprendimientoRepo = emprendimientoRepo;
        this.calificacionRepo   = calificacionRepo;
        this.pedidoRepo         = pedidoRepo;
        this.usuarioSyncRepo    = usuarioSyncRepo;
    }

    // ─────────────────────────────────────────────────────────────
    // USUARIO SYNC
    // ─────────────────────────────────────────────────────────────

    public UsuarioSyncDocument sincronizarUsuario(Usuario usuario) {
        UsuarioSyncDocument doc = usuarioSyncRepo
                .findByIdUsuarioSql(usuario.getIdUsuario())
                .orElse(new UsuarioSyncDocument());

        doc.setIdUsuarioSql(usuario.getIdUsuario());
        doc.setNombreCompleto(usuario.getNombreCompleto());
        doc.setCorreoInstitucional(usuario.getCorreoInstitucional());
        doc.setProgramaAcademico(usuario.getProgramaAcademico());
        doc.setFotoPerfil(usuario.getFotoPerfil());

        List<String> roles = usuario.getRoles() == null ? List.of() :
                usuario.getRoles().stream()
                        .map(ur -> ur.getRol().getNombre())
                        .collect(Collectors.toList());
        doc.setRoles(roles);

        Date now = new Date();
        if (doc.getCreatedAt() == null) doc.setCreatedAt(now);
        doc.setUpdatedAt(now);

        return usuarioSyncRepo.save(doc);
    }

    // ─────────────────────────────────────────────────────────────
    // EMPRENDIMIENTO SYNC
    // ─────────────────────────────────────────────────────────────

    public EmprendimientoDocument sincronizarEmprendimiento(Emprendimiento e) {
        EmprendimientoDocument doc = emprendimientoRepo
                .findByIdEmprendimientoSql(e.getIdEmprendimiento())
                .orElse(new EmprendimientoDocument());

        doc.setIdEmprendimientoSql(e.getIdEmprendimiento());
        doc.setNombre(e.getNombre());
        doc.setNombreEmprendimiento(e.getNombre());
        doc.setDescripcion(e.getDescripcion());
        doc.setLogoUrl(e.getLogoUrl());
        doc.setDestacado(e.getDestacado() != null && e.getDestacado());
        doc.setEstado(e.getEstado() != null ? e.getEstado().getNombre() : "ACTIVO");

        // Emprendedor embed
        if (e.getUsuario() != null) {
            doc.setEmprendedor(new EmprendimientoDocument.EmprendedorEmbed(
                    e.getUsuario().getIdUsuario(),
                    e.getUsuario().getNombreCompleto(),
                    e.getUsuario().getCorreoInstitucional()
            ));
        }

        // Categoria embed
        if (e.getCategoria() != null) {
            doc.setCategoria(new EmprendimientoDocument.CategoriaEmbed(
                    e.getCategoria().getIdCategoria(),
                    e.getCategoria().getNombre()
            ));
        }

        // Semestre embed
        if (e.getSemestre() != null) {
            doc.setSemestre(new EmprendimientoDocument.SemestreEmbed(
                    e.getSemestre().getIdSemestre(),
                    e.getSemestre().getPeriodo()
            ));
        }

        // Productos embebidos
        if (e.getProductos() != null) {
            List<EmprendimientoDocument.ProductoEmbed> productos = e.getProductos().stream()
                    .map(p -> {
                        List<String> imgs = p.getImagenes() == null ? List.of() :
                                p.getImagenes().stream()
                                        .map(ImagenProducto::getUrlImagen)
                                        .collect(Collectors.toList());
                        return new EmprendimientoDocument.ProductoEmbed(
                                p.getIdProducto(),
                                p.getNombre(),
                                p.getDescripcion(),
                                p.getPrecio(),
                                p.getStockDisponible(),
                                p.getEstado() != null ? p.getEstado().getNombre() : "ACTIVO",
                                imgs,
                                new Date(),
                                new Date()
                        );
                    }).collect(Collectors.toList());
            doc.setProductos(productos);
        }

        // Inicializar métricas si es nuevo
        if (doc.getMetricas() == null) {
            doc.setMetricas(new EmprendimientoDocument.MetricasEmbed(
                    0, BigDecimal.ZERO, 0, 0, 0.0,
                    new ArrayList<>(), new ArrayList<>()
            ));
        }

        if (doc.getUltimasCalificaciones() == null) {
            doc.setUltimasCalificaciones(new ArrayList<>());
        }

        Date now = new Date();
        if (doc.getCreatedAt() == null) doc.setCreatedAt(now);
        doc.setUpdatedAt(now);

        return emprendimientoRepo.save(doc);
    }

    // ─────────────────────────────────────────────────────────────
    // PEDIDO SYNC + ACTUALIZAR METRICAS
    // ─────────────────────────────────────────────────────────────

    public PedidoDocument sincronizarPedido(Pedido pedido) {
        PedidoDocument doc = pedidoRepo
                .findByIdPedidoSql(pedido.getIdPedido())
                .orElse(new PedidoDocument());

        doc.setIdPedidoSql(pedido.getIdPedido());

        // Referencia al emprendimiento en MongoDB
        if (pedido.getEmprendimiento() != null) {
            emprendimientoRepo.findByIdEmprendimientoSql(
                    pedido.getEmprendimiento().getIdEmprendimiento())
                    .ifPresent(emp -> doc.setEmprendimientoId(emp.getId()));
            doc.setIdEmprendimientoSql(pedido.getEmprendimiento().getIdEmprendimiento());
            doc.setNombreEmprendimiento(pedido.getEmprendimiento().getNombre());
        }

        // Comprador embed
        if (pedido.getUsuario() != null) {
            doc.setComprador(new PedidoDocument.CompradorEmbed(
                    pedido.getUsuario().getIdUsuario(),
                    pedido.getUsuario().getNombreCompleto(),
                    pedido.getUsuario().getCorreoInstitucional()
            ));
        }

        doc.setEstado(pedido.getEstado() != null ? pedido.getEstado().getNombre() : "PENDIENTE");
        doc.setTotal(pedido.getTotal());
        doc.setFechaPedido(pedido.getFechaPedido());

        // Detalles embebidos
        if (pedido.getDetalles() != null) {
            List<PedidoDocument.DetallePedidoEmbed> detalles = pedido.getDetalles().stream()
                    .map(d -> new PedidoDocument.DetallePedidoEmbed(
                            d.getIdDetalle(),
                            d.getProducto() != null ? d.getProducto().getIdProducto() : null,
                            d.getProducto() != null ? d.getProducto().getNombre() : "",
                            d.getCantidad(),
                            d.getPrecioUnitario(),
                            d.getSubtotal()
                    )).collect(Collectors.toList());
            doc.setDetalles(detalles);
        }

        Date now = new Date();
        if (doc.getCreatedAt() == null) doc.setCreatedAt(now);
        doc.setUpdatedAt(now);

        PedidoDocument saved = pedidoRepo.save(doc);

        // Trigger: actualizar metricas si el pedido es ENTREGADO
        if ("ENTREGADO".equalsIgnoreCase(doc.getEstado()) && pedido.getEmprendimiento() != null) {
            actualizarMetricasEmprendimiento(pedido.getEmprendimiento().getIdEmprendimiento());
        }

        return saved;
    }

    // ─────────────────────────────────────────────────────────────
    // CALIFICACION SYNC + ACTUALIZAR CACHE
    // ─────────────────────────────────────────────────────────────

    public CalificacionDocument sincronizarCalificacion(Calificacion calificacion) {
        CalificacionDocument doc = calificacionRepo
                .findByIdCalificacionSql(calificacion.getIdCalificacion())
                .orElse(new CalificacionDocument());

        doc.setIdCalificacionSql(calificacion.getIdCalificacion());
        doc.setIdPedidoSql(calificacion.getPedido() != null ? calificacion.getPedido().getIdPedido() : null);

        if (calificacion.getEmprendimiento() != null) {
            emprendimientoRepo.findByIdEmprendimientoSql(
                    calificacion.getEmprendimiento().getIdEmprendimiento())
                    .ifPresent(emp -> doc.setEmprendimientoId(emp.getId()));
            doc.setIdEmprendimientoSql(calificacion.getEmprendimiento().getIdEmprendimiento());
        }

        if (calificacion.getUsuario() != null) {
            doc.setUsuario(new CalificacionDocument.UsuarioEmbed(
                    calificacion.getUsuario().getIdUsuario(),
                    calificacion.getUsuario().getNombreCompleto()
            ));
        }

        doc.setPuntuacion(calificacion.getPuntuacion());
        doc.setComentario(calificacion.getComentario());
        doc.setFecha(calificacion.getFecha());

        Date now = new Date();
        if (doc.getCreatedAt() == null) doc.setCreatedAt(now);

        CalificacionDocument saved = calificacionRepo.save(doc);

        // Trigger: actualizar métricas y caché en emprendimiento
        if (calificacion.getEmprendimiento() != null) {
            actualizarMetricasCalificacion(
                    calificacion.getEmprendimiento().getIdEmprendimiento(), saved);
        }

        return saved;
    }

    // ─────────────────────────────────────────────────────────────
    // TRIGGERS — actualización automática de métricas
    // ─────────────────────────────────────────────────────────────

    /**
     * Trigger: recalcula métricas de pedidos del emprendimiento.
     * Se ejecuta cuando un pedido pasa a estado ENTREGADO.
     */
    public void actualizarMetricasEmprendimiento(Integer idEmprendimientoSql) {
        emprendimientoRepo.findByIdEmprendimientoSql(idEmprendimientoSql).ifPresent(emp -> {
            List<PedidoDocument> pedidosEntregados = pedidoRepo
                    .findByIdEmprendimientoSqlAndEstado(idEmprendimientoSql, "ENTREGADO");

            long totalPedidos = pedidosEntregados.size();
            BigDecimal ingresos = pedidosEntregados.stream()
                    .map(p -> p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Pedidos por mes
            Map<String, Integer> porMes = new TreeMap<>();
            pedidosEntregados.forEach(p -> {
                if (p.getFechaPedido() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(p.getFechaPedido());
                    String mes = cal.get(Calendar.YEAR) + "-"
                            + String.format("%02d", cal.get(Calendar.MONTH) + 1);
                    porMes.merge(mes, 1, Integer::sum);
                }
            });
            List<EmprendimientoDocument.PedidoMesEmbed> pedidosMes = porMes.entrySet().stream()
                    .map(e -> new EmprendimientoDocument.PedidoMesEmbed(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());

            // Productos mas vendidos
            Map<Integer, EmprendimientoDocument.ProductoVendidoEmbed> prodMap = new HashMap<>();
            pedidosEntregados.forEach(p -> {
                if (p.getDetalles() != null) {
                    p.getDetalles().forEach(d -> {
                        prodMap.compute(d.getIdProductoSql(), (k, v) -> {
                            if (v == null) v = new EmprendimientoDocument.ProductoVendidoEmbed(
                                    d.getIdProductoSql(), d.getNombreProducto(), 0, BigDecimal.ZERO);
                            v.setUnidadesVendidas(v.getUnidadesVendidas() + d.getCantidad());
                            v.setIngresos(v.getIngresos().add(d.getSubtotal() != null ? d.getSubtotal() : BigDecimal.ZERO));
                            return v;
                        });
                    });
                }
            });
            List<EmprendimientoDocument.ProductoVendidoEmbed> topProductos = prodMap.values().stream()
                    .sorted((a, b) -> b.getUnidadesVendidas() - a.getUnidadesVendidas())
                    .limit(5).collect(Collectors.toList());

            EmprendimientoDocument.MetricasEmbed metricas = emp.getMetricas() != null
                    ? emp.getMetricas() : new EmprendimientoDocument.MetricasEmbed();
            metricas.setTotalPedidos((int) totalPedidos);
            metricas.setIngresosAcumulados(ingresos);
            metricas.setPedidosPorMes(pedidosMes);
            metricas.setProductosMasVendidos(topProductos);

            emp.setMetricas(metricas);
            emp.setUpdatedAt(new Date());
            emprendimientoRepo.save(emp);
        });
    }

    /**
     * Trigger: actualiza el promedio de calificaciones y la cache
     * de últimas reseñas en el documento del emprendimiento.
     * Se ejecuta cada vez que se guarda una calificación nueva.
     */
    public void actualizarMetricasCalificacion(Integer idEmprendimientoSql,
                                                CalificacionDocument nuevaCal) {
        emprendimientoRepo.findByIdEmprendimientoSql(idEmprendimientoSql).ifPresent(emp -> {
            List<CalificacionDocument> todas = calificacionRepo
                    .findByIdEmprendimientoSql(idEmprendimientoSql);

            int totalCals = todas.size();
            int sumaPuntos = todas.stream().mapToInt(CalificacionDocument::getPuntuacion).sum();
            double promedio = totalCals > 0 ? (double) sumaPuntos / totalCals : 0.0;

            EmprendimientoDocument.MetricasEmbed metricas = emp.getMetricas() != null
                    ? emp.getMetricas() : new EmprendimientoDocument.MetricasEmbed();
            metricas.setTotalCalificaciones(totalCals);
            metricas.setSumaPuntuaciones(sumaPuntos);
            metricas.setCalificacionPromedio(Math.round(promedio * 10.0) / 10.0);

            // Cache de últimas 5 calificaciones
            List<EmprendimientoDocument.UltimaCalificacionEmbed> ultimas = todas.stream()
                    .sorted((a, b) -> b.getFecha() != null && a.getFecha() != null
                            ? b.getFecha().compareTo(a.getFecha()) : 0)
                    .limit(5)
                    .map(c -> new EmprendimientoDocument.UltimaCalificacionEmbed(
                            c.getIdCalificacionSql(),
                            c.getUsuario() != null ? c.getUsuario().getIdUsuarioSql() : null,
                            c.getUsuario() != null ? c.getUsuario().getNombreUsuario() : "",
                            c.getPuntuacion(),
                            c.getComentario(),
                            c.getFecha()
                    )).collect(Collectors.toList());

            emp.setMetricas(metricas);
            emp.setUltimasCalificaciones(ultimas);
            emp.setUpdatedAt(new Date());
            emprendimientoRepo.save(emp);
        });
    }
}
