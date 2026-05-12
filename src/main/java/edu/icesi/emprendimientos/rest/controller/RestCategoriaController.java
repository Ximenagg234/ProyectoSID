package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Categoria;
import edu.icesi.emprendimientos.rest.dto.CategoriaRequestDTO;
import edu.icesi.emprendimientos.rest.dto.CategoriaResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.CategoriaMapper;
import edu.icesi.emprendimientos.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "CRUD de categorías (solo ADMIN)")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class RestCategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaMapper categoriaMapper;

    public RestCategoriaController(CategoriaService categoriaService, CategoriaMapper categoriaMapper) {
        this.categoriaService = categoriaService;
        this.categoriaMapper = categoriaMapper;
    }

    @GetMapping
    @Operation(summary = "Listar categorías")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "403", description = "Sin permisos")
    public ResponseEntity<List<CategoriaResponseDTO>> getAll() {
        return ResponseEntity.ok(categoriaService.listar().stream()
                .map(categoriaMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "404", description = "No encontrada")
    public ResponseEntity<CategoriaResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaMapper.toDto(categoriaService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear categoría")
    @ApiResponse(responseCode = "201", description = "Creada")
    public ResponseEntity<CategoriaResponseDTO> create(@RequestBody CategoriaRequestDTO dto) {
        Categoria saved = categoriaService.guardar(categoriaMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría")
    @ApiResponse(responseCode = "200", description = "Actualizada")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Integer id,
                                                        @RequestBody CategoriaRequestDTO dto) {
        Categoria cat = categoriaService.buscarPorId(id);
        cat.setNombre(dto.getNombre());
        cat.setDescripcion(dto.getDescripcion());
        return ResponseEntity.ok(categoriaMapper.toDto(categoriaService.guardar(cat)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría")
    @ApiResponse(responseCode = "204", description = "Eliminada")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
