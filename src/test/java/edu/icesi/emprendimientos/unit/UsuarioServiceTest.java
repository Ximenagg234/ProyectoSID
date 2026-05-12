package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.entity.UsuarioRol;
import edu.icesi.emprendimientos.entity.keys.UsuarioRolId;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.repository.UsuarioRolRepository;
import edu.icesi.emprendimientos.service.impl.UsuarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioRolRepository usuarioRolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombreCompleto("Ximena Gomez");
        usuario.setCorreoInstitucional("ximena@icesi.edu.co");
        usuario.setClave("1234");
        usuario.setRoles(new ArrayList<>(Arrays.asList(new UsuarioRol())));
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void guardarUsuario_WhenTieneRol_SeGuardaCorrectamente() {
        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario result = usuarioService.guardar(usuario);

        assertNotNull(result);
        assertEquals("Ximena Gomez", result.getNombreCompleto());
    }

    @Test
    void guardarUsuario_WhenNoTieneRol_SeGuardaConListaVacia() {
        usuario.setRoles(null);
        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario result = usuarioService.guardar(usuario);

        assertNotNull(result);
        assertNotNull(result.getRoles());
    }

    @Test
    void guardarUsuario_WhenSinClave_LanzaExcepcion() {
        usuario.setClave(null);

        assertThrows(RuntimeException.class, () -> usuarioService.guardar(usuario));
    }

    // =========================
    // READ
    // =========================

    @Test
    void listarUsuarios_ReturnListaUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<Usuario> result = usuarioService.listar();

        assertEquals(1, result.size());
    }

    @Test
    void buscarUsuarioPorId_WhenExiste_ReturnUsuario() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.buscarPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdUsuario());
    }

    @Test
    void buscarUsuarioPorId_WhenNoExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.buscarPorId(1));
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void actualizarUsuario_WhenExiste_ActualizaCorrectamente() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("abcd")).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario actualizado = new Usuario();
        actualizado.setNombreCompleto("Nuevo Nombre");
        actualizado.setClave("abcd");

        Usuario result = usuarioService.actualizar(1, actualizado);

        assertEquals("Nuevo Nombre", result.getNombreCompleto());
    }

    @Test
    void actualizarUsuario_WhenNoExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.actualizar(1, usuario));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void eliminarUsuario_WhenExiste_EliminaCorrectamente() {
        doNothing().when(usuarioRepository).deleteById(1);

        usuarioService.eliminar(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarUsuario_WhenFalla_LanzaExcepcion() {
        doThrow(new RuntimeException()).when(usuarioRepository).deleteById(1);

        assertThrows(RuntimeException.class, () -> usuarioService.eliminar(1));
    }

    // =========================
    // ASIGNAR ROL
    // =========================

    @Test
    void asignarRol_UsuarioExiste_RolExiste_AsignaCorrectamente() {
        UsuarioRolId id = new UsuarioRolId(1, 1);
        Rol rol = new Rol();
        rol.setIdRol(1);

        when(usuarioRolRepository.existsById(id)).thenReturn(false);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(usuarioRolRepository.save(any(UsuarioRol.class))).thenReturn(new UsuarioRol());

        usuarioService.asignarRol(1, 1);

        verify(usuarioRolRepository, times(1)).save(any(UsuarioRol.class));
    }

    @Test
    void asignarRol_YaAsignado_NoGuardaDuplicado() {
        UsuarioRolId id = new UsuarioRolId(1, 1);
        when(usuarioRolRepository.existsById(id)).thenReturn(true);

        usuarioService.asignarRol(1, 1);

        verify(usuarioRolRepository, never()).save(any());
    }

    @Test
    void asignarRol_UsuarioNoExiste_LanzaExcepcion() {
        UsuarioRolId id = new UsuarioRolId(1, 1);
        when(usuarioRolRepository.existsById(id)).thenReturn(false);
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.asignarRol(1, 1));
    }

    @Test
    void asignarRol_RolNoExiste_LanzaExcepcion() {
        UsuarioRolId id = new UsuarioRolId(1, 1);
        when(usuarioRolRepository.existsById(id)).thenReturn(false);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> usuarioService.asignarRol(1, 1));
    }

    // =========================
    // QUITAR ROL
    // =========================

    @Test
    void quitarRol_QuitaCorrectamente() {
        UsuarioRolId id = new UsuarioRolId(1, 1);
        doNothing().when(usuarioRolRepository).deleteById(id);

        usuarioService.quitarRol(1, 1);

        verify(usuarioRolRepository, times(1)).deleteById(id);
    }

    @Test
    void quitarRol_WhenFalla_LanzaExcepcion() {
        UsuarioRolId id = new UsuarioRolId(1, 1);
        doThrow(new RuntimeException()).when(usuarioRolRepository).deleteById(id);

        assertThrows(RuntimeException.class, () -> usuarioService.quitarRol(1, 1));
    }
}
