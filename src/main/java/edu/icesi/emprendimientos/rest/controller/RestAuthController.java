package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.rest.dto.LoginRequestDTO;
import edu.icesi.emprendimientos.rest.dto.LoginResponseDTO;
import edu.icesi.emprendimientos.rest.dto.UsuarioRequestDTO;
import edu.icesi.emprendimientos.rest.dto.UsuarioResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.UsuarioMapper;
import edu.icesi.emprendimientos.rest.security.JwtService;
import edu.icesi.emprendimientos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación REST", description = "Login y registro vía JWT")
public class RestAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public RestAuthController(AuthenticationManager authenticationManager,
                              JwtService jwtService,
                              UsuarioService usuarioService,
                              UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario y obtener token JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreoInstitucional(), dto.getClave())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .collect(Collectors.toList());
        edu.icesi.emprendimientos.entity.Usuario usuario =
                usuarioService.buscarPorCorreo(userDetails.getUsername());
        Integer idUsuario = (usuario != null) ? usuario.getIdUsuario() : null;
        return ResponseEntity.ok(new LoginResponseDTO(token, "Bearer", userDetails.getUsername(), roles, idUsuario));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @ApiResponse(responseCode = "400", description = "Correo no institucional o datos inválidos")
    public ResponseEntity<?> register(
            @RequestBody UsuarioRequestDTO dto,
            @RequestParam(defaultValue = "3") int idRol) {
        try {
            Usuario usuario = usuarioMapper.toEntity(dto);
            Usuario saved = usuarioService.guardar(usuario);
            int rolId = (idRol == 2) ? 2 : 3;
            usuarioService.asignarRol(saved.getIdUsuario(), rolId);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(saved));
        } catch (IllegalArgumentException e) {
            // Error de validación de email institucional
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
