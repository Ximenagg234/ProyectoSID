package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.CalificacionProducto;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.ProductoRepository;
import edu.icesi.emprendimientos.rest.dto.CalificacionProductoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.CalificacionProductoResponseDTO;
import edu.icesi.emprendimientos.service.CalificacionProductoService;
import edu.icesi.emprendimientos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/productos")
@Tag(name = "Calificaciones de Productos", description = "Reseñas y valoraciones de productos")
@SecurityRequirement(name = "BearerAuth")
public class RestCalificacionProductoController {

    private final CalificacionProductoService calificacionProductoService;
    private final UsuarioService usuarioService;
    private final ProductoRepository productoRepository;

    public RestCalificacionProductoController(CalificacionProductoService calificacionProductoService,
                                              UsuarioService usuarioService,
                                              ProductoRepository productoRepository) {
        this.calificacionProductoService = calificacionProductoService;
        this.usuarioService = usuarioService;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/{idProducto}/calificaciones")
    @Operation(summary = "Obtener calificaciones de un producto")
    public ResponseEntity<List<CalificacionProductoResponseDTO>> getCalificaciones(
            @PathVariable Integer idProducto) {
        List<CalificacionProductoResponseDTO> lista = calificacionProductoService
                .listarPorProducto(idProducto).stream()
                .map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{idProducto}/calificaciones/promedio")
    @Operation(summary = "Promedio de calificaciones de un producto")
    public ResponseEntity<Map<String, Object>> getPromedio(@PathVariable Integer idProducto) {
        double promedio = calificacionProductoService.promedioPorProducto(idProducto);
        long total = calificacionProductoService.listarPorProducto(idProducto).size();
        return ResponseEntity.ok(Map.of("promedio", promedio, "total", total));
    }

    @GetMapping("/{idProducto}/ya-califico")
    @Operation(summary = "Verificar si el usuario ya califico este producto")
    public ResponseEntity<Map<String, Boolean>> yaCalificoProducto(
            @PathVariable Integer idProducto,
            @RequestParam Integer idUsuario) {
        boolean ya = calificacionProductoService.yaCalificoProducto(idUsuario, idProducto);
        return ResponseEntity.ok(Map.of("yaCalifico", ya));
    }

    @PostMapping("/{idProducto}/calificaciones")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Calificar un producto")
    public ResponseEntity<?> calificar(
            @PathVariable Integer idProducto,
            @RequestBody CalificacionProductoRequestDTO dto) {

        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5)
            return ResponseEntity.badRequest().body(Map.of("error", "La puntuación debe ser entre 1 y 5"));

        if (calificacionProductoService.yaCalificoProducto(dto.getIdUsuario(), idProducto))
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Ya calificaste este producto"));

        Producto producto = productoRepository.findById(idProducto)
                .orElse(null);
        if (producto == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Producto no encontrado"));

        Usuario usuario = usuarioService.buscarPorId(dto.getIdUsuario());

        CalificacionProducto cal = new CalificacionProducto();
        cal.setPuntuacion(dto.getPuntuacion());
        cal.setComentario(dto.getComentario());
        cal.setFecha(new Date());
        cal.setUsuario(usuario);
        cal.setProducto(producto);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(calificacionProductoService.guardar(cal)));
    }

    @PutMapping("/{idProducto}/calificaciones/{idCalificacion}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Editar una calificación propia de producto")
    public ResponseEntity<?> actualizar(
            @PathVariable Integer idProducto,
            @PathVariable Integer idCalificacion,
            @RequestBody CalificacionProductoRequestDTO dto) {
        if (dto.getPuntuacion() == null || dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5)
            return ResponseEntity.badRequest().body(Map.of("error", "La puntuación debe ser entre 1 y 5"));
        try {
            CalificacionProducto updated = calificacionProductoService.actualizar(
                    idCalificacion, dto.getIdUsuario(), dto.getPuntuacion(), dto.getComentario());
            return ResponseEntity.ok(toDto(updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{idProducto}/calificaciones/{idCalificacion}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Eliminar una calificación propia de producto")
    public ResponseEntity<?> eliminar(
            @PathVariable Integer idProducto,
            @PathVariable Integer idCalificacion,
            @RequestParam Integer idUsuario) {
        try {
            calificacionProductoService.eliminar(idCalificacion, idUsuario);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private CalificacionProductoResponseDTO toDto(CalificacionProducto c) {
        CalificacionProductoResponseDTO dto = new CalificacionProductoResponseDTO();
        dto.setIdCalificacionProducto(c.getIdCalificacionProducto());
        dto.setPuntuacion(c.getPuntuacion());
        dto.setComentario(c.getComentario());
        dto.setFecha(c.getFecha());
        dto.setIdUsuario(c.getUsuario() != null ? c.getUsuario().getIdUsuario() : null);
        dto.setNombreUsuario(c.getUsuario() != null ? c.getUsuario().getNombreCompleto() : "");
        dto.setIdProducto(c.getProducto() != null ? c.getProducto().getIdProducto() : null);
        return dto;
    }
}
