package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.rest.dto.UsuarioRequestDTO;
import edu.icesi.emprendimientos.rest.dto.UsuarioResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.UsuarioMapper;
import edu.icesi.emprendimientos.service.FileStorageService;
import edu.icesi.emprendimientos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "CRUD de usuarios")
@SecurityRequirement(name = "BearerAuth")
public class RestUsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final FileStorageService fileStorageService;

    public RestUsuarioController(UsuarioService usuarioService,
                                 UsuarioMapper usuarioMapper,
                                 FileStorageService fileStorageService) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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
        Usuario toUpdate = usuarioMapper.toEntity(dto);
        Usuario saved = usuarioService.actualizar(id, toUpdate);
        return ResponseEntity.ok(usuarioMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario (solo ADMIN)")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    @ApiResponse(responseCode = "403", description = "Sin permisos")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /** Asignar un rol a un usuario (solo ADMIN) */
    @PostMapping("/{id}/roles/{idRol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Asignar rol a usuario")
    @ApiResponse(responseCode = "200", description = "Rol asignado")
    public ResponseEntity<UsuarioResponseDTO> asignarRol(@PathVariable Integer id,
                                                         @PathVariable Integer idRol) {
        usuarioService.asignarRol(id, idRol);
        return ResponseEntity.ok(usuarioMapper.toDto(usuarioService.buscarPorId(id)));
    }

    /** Quitar un rol a un usuario (solo ADMIN) */
    @DeleteMapping("/{id}/roles/{idRol}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Quitar rol de usuario")
    @ApiResponse(responseCode = "200", description = "Rol quitado")
    public ResponseEntity<UsuarioResponseDTO> quitarRol(@PathVariable Integer id,
                                                        @PathVariable Integer idRol) {
        usuarioService.quitarRol(id, idRol);
        return ResponseEntity.ok(usuarioMapper.toDto(usuarioService.buscarPorId(id)));
    }

    @PostMapping(value = "/{id}/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir foto de perfil desde dispositivo")
    @ApiResponse(responseCode = "200", description = "Foto actualizada")
    public ResponseEntity<UsuarioResponseDTO> uploadFoto(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.guardar(file, "perfiles");
            if (url == null) return ResponseEntity.badRequest().build();
            Usuario u = usuarioService.buscarPorId(id);
            u.setFotoPerfil(url);
            Usuario saved = usuarioService.actualizar(id, u);
            return ResponseEntity.ok(usuarioMapper.toDto(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
