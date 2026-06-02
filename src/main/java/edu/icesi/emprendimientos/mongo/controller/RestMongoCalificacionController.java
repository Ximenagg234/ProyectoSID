package edu.icesi.emprendimientos.mongo.controller;

import edu.icesi.emprendimientos.mongo.document.CalificacionDocument;
import edu.icesi.emprendimientos.mongo.repository.CalificacionMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.EmprendimientoMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.PedidoMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.UsuarioSyncMongoRepository;
import edu.icesi.emprendimientos.mongo.service.MongoSyncService;
import edu.icesi.emprendimientos.rest.dto.CalificacionRequestDTO;
import edu.icesi.emprendimientos.rest.dto.CalificacionResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Profile("sid")
@RestController
@RequestMapping("/api")
@Tag(name = "Calificaciones MongoDB")
@SecurityRequirement(name = "BearerAuth")
public class RestMongoCalificacionController {

    private final CalificacionMongoRepository   calRepo;
    private final EmprendimientoMongoRepository empRepo;
    private final PedidoMongoRepository         pedidoRepo;
    private final UsuarioSyncMongoRepository    usuarioRepo;
    private final MongoSyncService              mongoSync;

    public RestMongoCalificacionController(CalificacionMongoRepository calRepo,
                                            EmprendimientoMongoRepository empRepo,
                                            PedidoMongoRepository pedidoRepo,
                                            UsuarioSyncMongoRepository usuarioRepo,
                                            MongoSyncService mongoSync) {
        this.calRepo    = calRepo;
        this.empRepo    = empRepo;
        this.pedidoRepo = pedidoRepo;
        this.usuarioRepo = usuarioRepo;
        this.mongoSync  = mongoSync;
    }

    @GetMapping("/emprendimientos/{id}/calificaciones")
    public ResponseEntity<List<CalificacionResponseDTO>> getCalificaciones(@PathVariable Integer id) {
        return ResponseEntity.ok(
            calRepo.findByIdEmprendimientoSql(id).stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/emprendimientos/{id}/calificaciones/promedio")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Integer id) {
        List<CalificacionDocument> lista = calRepo.findByIdEmprendimientoSql(id);
        double prom = lista.stream().mapToInt(CalificacionDocument::getPuntuacion).average().orElse(0.0);
        return ResponseEntity.ok(Map.of("promedio", prom, "total", (long) lista.size()));
    }

    @GetMapping("/pedidos/{idPedido}/ya-califico")
    public ResponseEntity<Map<String, Boolean>> yaCalifico(@PathVariable Integer idPedido) {
        boolean ya = calRepo.findAll().stream()
            .anyMatch(c -> idPedido.equals(c.getIdPedidoSql()));
        return ResponseEntity.ok(Map.of("yaCalifico", ya));
    }

    @PostMapping("/emprendimientos/{idEmp}/calificaciones")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> calificar(@PathVariable Integer idEmp,
                                        @RequestBody CalificacionRequestDTO dto) {
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5)
            return ResponseEntity.badRequest().body(Map.of("error", "Puntuación debe ser 1-5"));

        boolean yaCalif = calRepo.findAll().stream()
            .anyMatch(c -> dto.getIdPedido().equals(c.getIdPedidoSql()));
        if (yaCalif) return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of("error", "Este pedido ya fue calificado"));

        CalificacionDocument cal = new CalificacionDocument();
        int newId = calRepo.findAll().stream()
            .mapToInt(c -> c.getIdCalificacionSql() != null ? c.getIdCalificacionSql() : 0)
            .max().orElse(100) + 1;
        cal.setIdCalificacionSql(newId);
        cal.setIdEmprendimientoSql(idEmp);
        cal.setIdPedidoSql(dto.getIdPedido());
        empRepo.findByIdEmprendimientoSql(idEmp).ifPresent(e -> cal.setEmprendimientoId(e.getId()));
        usuarioRepo.findByIdUsuarioSql(dto.getIdUsuario()).ifPresent(u ->
            cal.setUsuario(new CalificacionDocument.UsuarioEmbed(u.getIdUsuarioSql(), u.getNombreCompleto())));
        cal.setPuntuacion(dto.getPuntuacion());
        cal.setComentario(dto.getComentario());
        cal.setFecha(new Date());
        cal.setCreatedAt(new Date());
        CalificacionDocument saved = calRepo.save(cal);
        try { mongoSync.actualizarMetricasCalificacion(idEmp, saved); } catch (Exception ex) { /* no bloquea */ }
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/emprendimientos/{idEmp}/calificaciones/{idCal}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> actualizar(@PathVariable Integer idEmp, @PathVariable Integer idCal,
                                         @RequestBody CalificacionRequestDTO dto) {
        return calRepo.findByIdCalificacionSql(idCal).map(cal -> {
            if (!dto.getIdUsuario().equals(cal.getUsuario() != null ? cal.getUsuario().getIdUsuarioSql() : -1))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body((Object) Map.of("error", "Sin permisos"));
            cal.setPuntuacion(dto.getPuntuacion());
            cal.setComentario(dto.getComentario());
            CalificacionDocument saved = calRepo.save(cal);
            try { mongoSync.actualizarMetricasCalificacion(idEmp, saved); } catch (Exception ex) { /* no bloquea */ }
            return ResponseEntity.ok((Object) toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/emprendimientos/{idEmp}/calificaciones/{idCal}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminar(@PathVariable Integer idEmp, @PathVariable Integer idCal,
                                       @RequestParam Integer idUsuario) {
        return calRepo.findByIdCalificacionSql(idCal).map(cal -> {
            if (!idUsuario.equals(cal.getUsuario() != null ? cal.getUsuario().getIdUsuarioSql() : -1))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body((Object) Map.of("error", "Sin permisos"));
            calRepo.delete(cal);
            try { mongoSync.actualizarMetricasCalificacion(idEmp, null); } catch (Exception ex) { /* no bloquea */ }
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private CalificacionResponseDTO toDto(CalificacionDocument c) {
        CalificacionResponseDTO dto = new CalificacionResponseDTO();
        dto.setIdCalificacion(c.getIdCalificacionSql());
        dto.setPuntuacion(c.getPuntuacion());
        dto.setComentario(c.getComentario());
        dto.setFecha(c.getFecha());
        dto.setIdUsuario(c.getUsuario() != null ? c.getUsuario().getIdUsuarioSql() : null);
        dto.setNombreUsuario(c.getUsuario() != null ? c.getUsuario().getNombreUsuario() : "");
        dto.setIdEmprendimiento(c.getIdEmprendimientoSql());
        dto.setIdPedido(c.getIdPedidoSql());
        return dto;
    }
}
