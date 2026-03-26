package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.entity.UsuarioRol;
import edu.icesi.emprendimientos.repository.UsuarioRepository;
import edu.icesi.emprendimientos.service.impl.UsuarioServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioRol usuarioRol;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombreCompleto("Ximena Gomez");
        usuario.setClave("1234");

        usuarioRol = new UsuarioRol();

        usuario.setRoles(Arrays.asList(usuarioRol));
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void guardarUsuario_WhenTieneRol_SeGuardaCorrectamente() {
        // Arrange
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Act
        Usuario result = usuarioService.guardar(usuario);

        // Assert
        assertNotNull(result);
        assertEquals("Ximena Gomez", result.getNombreCompleto());
    }

    @Test
    void guardarUsuario_WhenNoTieneRol_LanzaExcepcion() {
        // Arrange
        usuario.setRoles(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.guardar(usuario));
    }

    // =========================
    // READ
    // =========================

    @Test
    void listarUsuarios_ReturnListaUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        // Act
        List<Usuario> result = usuarioService.listar();

        // Assert
        assertEquals(1, result.size());
    }

    // =========================
    // READ
    // =========================

    @Test
    void buscarUsuarioPorId_WhenExiste_ReturnUsuario() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        // Act
        Usuario result = usuarioService.buscarPorId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIdUsuario());
    }

    @Test
    void buscarUsuarioPorId_WhenNoExiste_LanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.buscarPorId(1));
    }

    // =========================
    //  UPDATE
    // =========================

    @Test
    void actualizarUsuario_WhenExiste_ActualizaCorrectamente() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario actualizado = new Usuario();
        actualizado.setNombreCompleto("Nuevo Nombre");
        actualizado.setClave("abcd");

        // Act
        Usuario result = usuarioService.actualizar(1, actualizado);

        // Assert
        assertEquals("Nuevo Nombre", result.getNombreCompleto());
    }

    @Test
    void actualizarUsuario_WhenNoExiste_LanzaExcepcion() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.actualizar(1, usuario));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void eliminarUsuario_WhenExiste_EliminaCorrectamente() {
        // Arrange
        doNothing().when(usuarioRepository).deleteById(1);

        // Act
        usuarioService.eliminar(1);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarUsuario_WhenFalla_LanzaExcepcion() {
        // Arrange
        doThrow(new RuntimeException()).when(usuarioRepository).deleteById(1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.eliminar(1));
    }
}