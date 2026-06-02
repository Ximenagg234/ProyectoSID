package edu.icesi.emprendimientos.mongo.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.mongo.document.EmprendimientoDocument;
import edu.icesi.emprendimientos.mongo.document.PedidoDocument;
import edu.icesi.emprendimientos.mongo.repository.EmprendimientoMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.PedidoMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.UsuarioSyncMongoRepository;
import edu.icesi.emprendimientos.mongo.service.MongoSyncService;
import edu.icesi.emprendimientos.rest.dto.PedidoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.PedidoResponseDTO;
import edu.icesi.emprendimientos.rest.dto.DetallePedidoResponseDTO;
import edu.icesi.emprendimientos.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Profile("sid")
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos MongoDB")
@SecurityRequirement(name = "BearerAuth")
public class RestMongoPedidoController {

    private final PedidoMongoRepository         pedidoRepo;
    private final EmprendimientoMongoRepository  empRepo;
    private final UsuarioSyncMongoRepository     usuarioSyncRepo;
    private final UsuarioService                 usuarioService;
    private final MongoSyncService               mongoSync;
    private final SimpMessagingTemplate          messagingTemplate;

    public RestMongoPedidoController(PedidoMongoRepository pedidoRepo,
                                      EmprendimientoMongoRepository empRepo,
                                      UsuarioSyncMongoRepository usuarioSyncRepo,
                                      UsuarioService usuarioService,
                                      MongoSyncService mongoSync,
                                      SimpMessagingTemplate messagingTemplate) {
        this.pedidoRepo       = pedidoRepo;
        this.empRepo          = empRepo;
        this.usuarioSyncRepo  = usuarioSyncRepo;
        this.usuarioService   = usuarioService;
        this.mongoSync        = mongoSync;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong-pedidos");
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> getMisPedidos() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario user = usuarioService.buscarPorCorreo(email);
            return ResponseEntity.ok(
                pedidoRepo.findByCompradorIdUsuarioSql(user.getIdUsuario())
                    .stream().map(this::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/recibidos")
    public ResponseEntity<List<PedidoResponseDTO>> getPedidosRecibidos() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario user = usuarioService.buscarPorCorreo(email);
            List<EmprendimientoDocument> misEmps = empRepo.findByEmprendedorIdUsuarioSql(user.getIdUsuario());
            List<PedidoDocument> pedidos = new ArrayList<>();
            misEmps.forEach(emp -> pedidos.addAll(pedidoRepo.findByEmprendimientoId(emp.getId())));
            return ResponseEntity.ok(pedidos.stream().map(this::toDto).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO dto) {
        PedidoDocument pedido = new PedidoDocument();
        int newId = pedidoRepo.findAll().stream()
            .mapToInt(p -> p.getIdPedidoSql() != null ? p.getIdPedidoSql() : 0).max().orElse(200) + 1;
        pedido.setIdPedidoSql(newId);

        empRepo.findByIdEmprendimientoSql(dto.getIdEmprendimiento()).ifPresent(emp -> {
            pedido.setEmprendimientoId(emp.getId());
            pedido.setIdEmprendimientoSql(emp.getIdEmprendimientoSql());
            pedido.setNombreEmprendimiento(emp.getNombre());
        });
        usuarioSyncRepo.findByIdUsuarioSql(dto.getIdUsuario()).ifPresent(u ->
            pedido.setComprador(new PedidoDocument.CompradorEmbed(
                u.getIdUsuarioSql(), u.getNombreCompleto(), u.getCorreoInstitucional())));
        pedido.setEstado("PENDIENTE");
        pedido.setFechaPedido(new Date());

        if (dto.getDetalles() != null) {
            BigDecimal total = BigDecimal.ZERO;
            List<PedidoDocument.DetallePedidoEmbed> detalles = new ArrayList<>();
            int idx = 1;
            for (var d : dto.getDetalles()) {
                PedidoDocument.DetallePedidoEmbed det = new PedidoDocument.DetallePedidoEmbed();
                det.setIdDetalleSql(idx++);
                det.setIdProductoSql(d.getIdProducto());
                det.setCantidad(d.getCantidad());
                det.setPrecioUnitario(d.getPrecioUnitario());
                BigDecimal sub = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
                det.setSubtotal(sub);
                total = total.add(sub);
                final int idP = d.getIdProducto();
                empRepo.findByIdEmprendimientoSql(dto.getIdEmprendimiento()).ifPresent(emp -> {
                    if (emp.getProductos() != null) emp.getProductos().stream()
                        .filter(p -> idP == p.getIdProductoSql()).findFirst()
                        .ifPresent(p -> det.setNombreProducto(p.getNombre()));
                });
                detalles.add(det);
            }
            pedido.setDetalles(detalles);
            pedido.setTotal(total);
        }

        pedido.setCreatedAt(new Date());
        pedido.setUpdatedAt(new Date());
        PedidoDocument saved = pedidoRepo.save(pedido);

        try { messagingTemplate.convertAndSend("/topic/notificaciones",
            Map.of("mensaje", "Nuevo pedido recibido en " + saved.getNombreEmprendimiento())); }
        catch (Exception ex) {}

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Integer idPedido,
                                                            @RequestBody Map<String, Integer> body) {
        Map<Integer, String> ESTADOS = Map.of(3,"PENDIENTE",4,"CONFIRMADO",5,"PREPARANDO",6,"ENTREGADO",7,"CANCELADO");
        Integer idEstado = body.get("idEstado");
        return pedidoRepo.findByIdPedidoSql(idPedido).map(p -> {
            String estado = idEstado != null ? ESTADOS.getOrDefault(idEstado, "PENDIENTE") : "PENDIENTE";
            p.setEstado(estado);
            p.setUpdatedAt(new Date());
            PedidoDocument saved = pedidoRepo.save(p);
            if ("ENTREGADO".equals(estado) && p.getIdEmprendimientoSql() != null) {
                try { mongoSync.actualizarMetricasEmprendimiento(p.getIdEmprendimientoSql()); } catch (Exception ex) {}
            }
            return ResponseEntity.ok(toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    PedidoResponseDTO toDto(PedidoDocument p) {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setIdPedido(p.getIdPedidoSql());
        dto.setIdEmprendimiento(p.getIdEmprendimientoSql());
        dto.setNombreEmprendimiento(p.getNombreEmprendimiento());
        dto.setNombreUsuario(p.getComprador() != null ? p.getComprador().getNombreUsuario() : "");
        dto.setNombreEstado(p.getEstado() != null ? p.getEstado() : "PENDIENTE");
        dto.setFechaPedido(p.getFechaPedido());
        dto.setTotal(p.getTotal());
        if (p.getDetalles() != null) {
            dto.setDetalles(p.getDetalles().stream().map(d -> {
                DetallePedidoResponseDTO det = new DetallePedidoResponseDTO();
                det.setIdDetalle(d.getIdDetalleSql());
                det.setIdProducto(d.getIdProductoSql());
                det.setNombreProducto(d.getNombreProducto());
                det.setCantidad(d.getCantidad());
                det.setPrecioUnitario(d.getPrecioUnitario());
                det.setSubtotal(d.getSubtotal());
                return det;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
