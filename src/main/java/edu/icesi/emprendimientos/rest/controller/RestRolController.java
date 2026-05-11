package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.rest.dto.RolRequestDTO;
import edu.icesi.emprendimientos.rest.dto.RolResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.RolMapper;
import edu.icesi.emprendimientos.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "CRUD de roles (solo ADMIN)")
@SecurityRequirement(name = "BearerAuth")
public class RestRolController {

    private final RolService rolService;
    private final RolMapper rolMapper;

    public RestRolController(RolService rolService, RolMapper rolMapper) {
        this.rolService = rolService;
        this.rolMapper = rolMapper;
    }

    @GetMapping
    @Operation(summary = "Listar roles")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "403", description = "Sin permisos")
    public ResponseEntity<List<RolResponseDTO>> getAll() {
        return ResponseEntity.ok(rolService.listar().stream()
                .map(rolMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<RolResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(rolMapper.toDto(rolService.buscarPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear rol")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<RolResponseDTO> create(@RequestBody RolRequestDTO dto) {
        Rol saved = rolService.guardar(rolMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(rolMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    public ResponseEntity<RolResponseDTO> update(@PathVariable Integer id, @RequestBody RolRequestDTO dto) {
        Rol rol = rolService.buscarPorId(id);
        rol.setNombre(dto.getNombre());
        return ResponseEntity.ok(rolMapper.toDto(rolService.guardar(rol)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
