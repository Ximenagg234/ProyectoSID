package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Categoria;
import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.entity.Semestre;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.repository.CategoriaRepository;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.repository.SemestreRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.rest.dto.EmprendimientoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.EmprendimientoResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.EmprendimientoMapper;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emprendimientos")
@Tag(name = "Emprendimientos", description = "CRUD de emprendimientos")
@SecurityRequirement(name = "BearerAuth")
public class RestEmprendimientoController {

    private final EmprendimientoService emprendimientoService;
    private final EmprendimientoMapper emprendimientoMapper;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final SemestreRepository semestreRepository;
    private final EstadoRepository estadoRepository;

    public RestEmprendimientoController(EmprendimientoService emprendimientoService,
                                         EmprendimientoMapper emprendimientoMapper,
                                         UsuarioRepository usuarioRepository,
                                         CategoriaRepository categoriaRepository,
                                         SemestreRepository semestreRepository,
                                         EstadoRepository estadoRepository) {
        this.emprendimientoService = emprendimientoService;
        this.emprendimientoMapper = emprendimientoMapper;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.semestreRepository = semestreRepository;
        this.estadoRepository = estadoRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los emprendimientos")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    public ResponseEntity<List<EmprendimientoResponseDTO>> getAll() {
        return ResponseEntity.ok(emprendimientoService.listar().stream()
                .map(emprendimientoMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener emprendimiento por ID")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<EmprendimientoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(emprendimientoMapper.toDto(emprendimientoService.buscarPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Crear emprendimiento")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<EmprendimientoResponseDTO> create(@RequestBody EmprendimientoRequestDTO dto) {
        Emprendimiento e = emprendimientoMapper.toEntity(dto);
        resolveRelations(e, dto);
        Emprendimiento saved = emprendimientoService.guardar(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(emprendimientoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Actualizar emprendimiento")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<EmprendimientoResponseDTO> update(@PathVariable Integer id,
                                                             @RequestBody EmprendimientoRequestDTO dto) {
        Emprendimiento saved = emprendimientoService.actualizar(id, buildFromDto(dto));
        return ResponseEntity.ok(emprendimientoMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Eliminar emprendimiento")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        emprendimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void resolveRelations(Emprendimiento e, EmprendimientoRequestDTO dto) {
        if (dto.getIdUsuario() != null)
            e.setUsuario(usuarioRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + dto.getIdUsuario())));
        if (dto.getIdCategoria() != null)
            e.setCategoria(categoriaRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada: " + dto.getIdCategoria())));
        if (dto.getIdSemestre() != null)
            e.setSemestre(semestreRepository.findById(dto.getIdSemestre())
                    .orElseThrow(() -> new EntityNotFoundException("Semestre no encontrado: " + dto.getIdSemestre())));
        if (dto.getIdEstado() != null)
            e.setEstado(estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + dto.getIdEstado())));
    }

    private Emprendimiento buildFromDto(EmprendimientoRequestDTO dto) {
        Emprendimiento e = new Emprendimiento();
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setLogoUrl(dto.getLogoUrl());
        resolveRelations(e, dto);
        return e;
    }
}
