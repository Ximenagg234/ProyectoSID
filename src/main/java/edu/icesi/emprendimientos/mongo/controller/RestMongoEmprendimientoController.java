package edu.icesi.emprendimientos.mongo.controller;

import edu.icesi.emprendimientos.mongo.document.EmprendimientoDocument;
import edu.icesi.emprendimientos.mongo.repository.EmprendimientoMongoRepository;
import edu.icesi.emprendimientos.mongo.repository.UsuarioSyncMongoRepository;
import edu.icesi.emprendimientos.rest.dto.EmprendimientoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.EmprendimientoResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Profile("sid")
@RestController
@RequestMapping("/api/emprendimientos")
@Tag(name = "Emprendimientos MongoDB", description = "CRUD de emprendimientos desde MongoDB")
@SecurityRequirement(name = "BearerAuth")
public class RestMongoEmprendimientoController {

    // Mapa de categorías (id → nombre)
    private static final Map<Integer, String> CATEGORIAS = Map.of(
        1, "Tecnologia", 2, "Moda", 3, "Comida",
        4, "Bebidas",   5, "Arte",  6, "Servicios"
    );

    private final EmprendimientoMongoRepository empRepo;
    private final UsuarioSyncMongoRepository     usuarioSyncRepo;

    public RestMongoEmprendimientoController(EmprendimientoMongoRepository empRepo,
                                              UsuarioSyncMongoRepository usuarioSyncRepo) {
        this.empRepo        = empRepo;
        this.usuarioSyncRepo = usuarioSyncRepo;
    }

    @GetMapping
    public ResponseEntity<List<EmprendimientoResponseDTO>> getAll() {
        return ResponseEntity.ok(
            empRepo.findAll().stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprendimientoResponseDTO> getById(@PathVariable Integer id) {
        return empRepo.findByIdEmprendimientoSql(id)
            .map(e -> ResponseEntity.ok(toDto(e)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    public ResponseEntity<EmprendimientoResponseDTO> create(@RequestBody EmprendimientoRequestDTO dto) {
        EmprendimientoDocument e = new EmprendimientoDocument();
        // Generate new ID
        int newId = empRepo.findAll().stream()
            .mapToInt(x -> x.getIdEmprendimientoSql() != null ? x.getIdEmprendimientoSql() : 0)
            .max().orElse(100) + 1;
        e.setIdEmprendimientoSql(newId);
        applyDto(e, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(empRepo.save(e)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    public ResponseEntity<EmprendimientoResponseDTO> update(@PathVariable Integer id,
                                                             @RequestBody EmprendimientoRequestDTO dto) {
        return empRepo.findByIdEmprendimientoSql(id)
            .map(e -> { applyDto(e, dto); return ResponseEntity.ok(toDto(empRepo.save(e))); })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        empRepo.findByIdEmprendimientoSql(id).ifPresent(empRepo::delete);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmprendimientoResponseDTO> cambiarEstado(@PathVariable Integer id,
                                                                    @RequestParam Integer idEstado) {
        return empRepo.findByIdEmprendimientoSql(id).map(e -> {
            e.setEstado(idEstado == 1 ? "ACTIVO" : "INACTIVO");
            e.setUpdatedAt(new Date());
            return ResponseEntity.ok(toDto(empRepo.save(e)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/destacado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmprendimientoResponseDTO> toggleDestacado(@PathVariable Integer id,
                                                                       @RequestParam Boolean destacado) {
        return empRepo.findByIdEmprendimientoSql(id).map(e -> {
            e.setDestacado(destacado);
            e.setUpdatedAt(new Date());
            return ResponseEntity.ok(toDto(empRepo.save(e)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void applyDto(EmprendimientoDocument e, EmprendimientoRequestDTO dto) {
        e.setNombre(dto.getNombre());
        e.setNombreEmprendimiento(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setLogoUrl(dto.getLogoUrl());
        e.setEstado(dto.getIdEstado() != null && dto.getIdEstado() == 2 ? "INACTIVO" : "ACTIVO");
        e.setDestacado(false);
        // Emprendedor
        if (dto.getIdUsuario() != null) {
            usuarioSyncRepo.findByIdUsuarioSql(dto.getIdUsuario()).ifPresent(u ->
                e.setEmprendedor(new EmprendimientoDocument.EmprendedorEmbed(
                    u.getIdUsuarioSql(), u.getNombreCompleto(), u.getCorreoInstitucional())));
        }
        // Categoria
        String catNombre = CATEGORIAS.getOrDefault(dto.getIdCategoria(), "General");
        e.setCategoria(new EmprendimientoDocument.CategoriaEmbed(dto.getIdCategoria(), catNombre));
        // Semestre
        e.setSemestre(new EmprendimientoDocument.SemestreEmbed(
            dto.getIdSemestre() != null ? dto.getIdSemestre() : 1, "2026-1"));
        if (e.getProductos() == null) e.setProductos(new ArrayList<>());
        if (e.getMetricas() == null)  e.setMetricas(new EmprendimientoDocument.MetricasEmbed(
            0, BigDecimal.ZERO, 0, 0, 0.0, new ArrayList<>(), new ArrayList<>()));
        if (e.getUltimasCalificaciones() == null) e.setUltimasCalificaciones(new ArrayList<>());
        Date now = new Date();
        if (e.getCreatedAt() == null) e.setCreatedAt(now);
        e.setUpdatedAt(now);
    }

    EmprendimientoResponseDTO toDto(EmprendimientoDocument e) {
        EmprendimientoResponseDTO dto = new EmprendimientoResponseDTO();
        dto.setIdEmprendimiento(e.getIdEmprendimientoSql());
        dto.setNombre(e.getNombre());
        dto.setDescripcion(e.getDescripcion());
        dto.setLogoUrl(e.getLogoUrl());
        dto.setDestacado(Boolean.TRUE.equals(e.getDestacado()));
        dto.setNombreUsuario(e.getEmprendedor() != null ? e.getEmprendedor().getNombreUsuario() : "");
        dto.setNombreCategoria(e.getCategoria() != null ? e.getCategoria().getNombre() : "");
        dto.setNombreSemestre(e.getSemestre() != null ? e.getSemestre().getNombre() : "2026-1");
        dto.setNombreEstado(e.getEstado() != null ? e.getEstado() : "ACTIVO");
        return dto;
    }
}
