package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Calificacion;
import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.EmprendimientoRepository;
import edu.icesi.emprendimientos.rest.dto.CalificacionRequestDTO;
import edu.icesi.emprendimientos.rest.dto.CalificacionResponseDTO;
import edu.icesi.emprendimientos.service.CalificacionService;
import edu.icesi.emprendimientos.service.PedidoService;
import edu.icesi.emprendimientos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;

@Profile("!sid")
@RestController
@RequestMapping("/api")
@Tag(name = "Calificaciones", description = "Valoraciones y comentarios de emprendimientos")
@SecurityRequirement(name = "BearerAuth")
public class RestCalificacionController {

    private final CalificacionService calificacionService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final EmprendimientoRepository emprendimientoRepository;

    public RestCalificacionController(CalificacionService calificacionService,
                                      UsuarioService usuarioService,
                                      PedidoService pedidoService,
                                      EmprendimientoRepository emprendimientoRepository) {
        this.calificacionService = calificacionService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.emprendimientoRepository = emprendimientoRepository;
    }

    /** Listar calificaciones de un emprendimiento */
    @GetMapping("/emprendimientos/{idEmprendimiento}/calificaciones")
    @Operation(summary = "Obtener calificaciones de un emprendimiento")
    public ResponseEntity<List<CalificacionResponseDTO>> getCalificaciones(
            @PathVariable Integer idEmprendimiento) {
        List<CalificacionResponseDTO> lista = calificacionService
                .listarPorEmprendimiento(idEmprendimiento)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    /** Promedio de calificaciones de un emprendimiento */
    @GetMapping("/emprendimientos/{idEmprendimiento}/calificaciones/promedio")
    @Operation(summary = "Promedio de calificaciones de un emprendimiento")
    public ResponseEntity<Map<String, Object>> getPromedio(
            @PathVariable Integer idEmprendimiento) {
        double promedio = calificacionService.promedioPorEmprendimiento(idEmprendimiento);
        long total = calificacionService.listarPorEmprendimiento(idEmprendimiento).size();
        return ResponseEntity.ok(Map.of("promedio", promedio, "total", total));
    }

    /** Verificar si un pedido ya fue calificado */
    @GetMapping("/pedidos/{idPedido}/ya-califico")
    @Operation(summary = "¿Este pedido ya tiene calificación?")
    public ResponseEntity<Map<String, Boolean>> yaCalifico(@PathVariable Integer idPedido) {
        return ResponseEntity.ok(Map.of("yaCalifico", calificacionService.yaCalifico(idPedido)));
    }

    /** Calificar un emprendimiento (solo para pedidos ENTREGADO no calificados) */
    @PostMapping("/emprendimientos/{idEmprendimiento}/calificaciones")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Calificar un emprendimiento")
    public ResponseEntity<?> calificar(
            @PathVariable Integer idEmprendimiento,
            @RequestBody CalificacionRequestDTO dto) {

        // Validar puntuacion 1-5
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La puntuación debe ser entre 1 y 5"));
        }

        // Verificar que el pedido existe y está ENTREGADO
        Pedido pedido;
        try {
            pedido = pedidoService.buscarPorId(dto.getIdPedido());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Pedido no encontrado"));
        }

        if (pedido.getEstado() == null ||
                !"ENTREGADO".equalsIgnoreCase(pedido.getEstado().getNombre())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Solo puedes calificar pedidos entregados"));
        }

        // Verificar que no haya sido calificado ya
        if (calificacionService.yaCalifico(dto.getIdPedido())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Este pedido ya fue calificado"));
        }

        // Obtener entidades relacionadas
        Usuario usuario = usuarioService.buscarPorId(dto.getIdUsuario());
        Emprendimiento emprendimiento = emprendimientoRepository.findById(idEmprendimiento)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));

        Calificacion cal = new Calificacion();
        cal.setPuntuacion(dto.getPuntuacion());
        cal.setComentario(dto.getComentario());
        cal.setFecha(new Date());
        cal.setUsuario(usuario);
        cal.setEmprendimiento(emprendimiento);
        cal.setPedido(pedido);

        Calificacion saved = calificacionService.guardar(cal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    private CalificacionResponseDTO toDto(Calificacion c) {
        CalificacionResponseDTO dto = new CalificacionResponseDTO();
        dto.setIdCalificacion(c.getIdCalificacion());
        dto.setPuntuacion(c.getPuntuacion());
        dto.setComentario(c.getComentario());
        dto.setFecha(c.getFecha());
        dto.setNombreUsuario(c.getUsuario() != null ? c.getUsuario().getNombreCompleto() : "");
        dto.setIdEmprendimiento(c.getEmprendimiento() != null
                ? c.getEmprendimiento().getIdEmprendimiento() : null);
        dto.setIdPedido(c.getPedido() != null ? c.getPedido().getIdPedido() : null);
        return dto;
    }
}
