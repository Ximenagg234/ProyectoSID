package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.rest.dto.UsuarioRequestDTO;
import edu.icesi.emprendimientos.rest.dto.UsuarioResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.UsuarioMapper;
import edu.icesi.emprendimientos.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios")
@SecurityRequirement(name = "BearerAuth")
public class RestUsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public RestUsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<UsuarioResponseDTO> list = usuarioService.listar().stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Integer id) {
        Usuario u = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuarioMapper.toDto(u));
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<UsuarioResponseDTO> create(@RequestBody UsuarioRequestDTO dto) {
        Usuario saved = usuarioService.guardar(usuarioMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Integer id,
                                                      @RequestBody UsuarioRequestDTO dto) {
        Usuario existing = usuarioService.buscarPorId(id);
        existing.setNombreCompleto(dto.getNombreCompleto());
        existing.setProgramaAcademico(dto.getProgramaAcademico());
        existing.setSemestreAcademico(dto.getSemestreAcademico());
        existing.setFotoPerfil(dto.getFotoPerfil());
        Usuario saved = usuarioService.guardar(existing);
        return ResponseEntity.ok(usuarioMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (solo ADMIN)")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    @ApiResponse(responseCode = "403", description = "Sin permisos")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
