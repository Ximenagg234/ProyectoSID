package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Permission;
import edu.icesi.emprendimientos.repository.PermissionRepository;
import edu.icesi.emprendimientos.service.impl.PermissionServiceImpl;

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
public class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionServiceImpl permissionService;

    private Permission permission;

    @BeforeEach
    void setup() {
        permission = new Permission();
        permission.setIdPermission(1);
        permission.setNombre("CREAR_USUARIO");
        permission.setDescripcion("Permite crear usuarios");
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void guardarPermission_WhenValido_SeGuardaCorrectamente() {
        // Arrange
        when(permissionRepository.save(permission)).thenReturn(permission);

        // Act
        Permission result = permissionService.guardar(permission);

        // Assert
        assertNotNull(result);
        assertEquals("CREAR_USUARIO", result.getNombre());
    }

    // =========================
    // READ
    // =========================

    @Test
    void listarPermissions_ReturnLista() {
        // Arrange
        when(permissionRepository.findAll()).thenReturn(Arrays.asList(permission));

        // Act
        List<Permission> result = permissionService.listar();

        // Assert
        assertEquals(1, result.size());
    }

    // =========================
    // READ
    // =========================

    @Test
    void buscarPermissionPorId_WhenExiste_ReturnPermission() {
        // Arrange
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));

        // Act
        Permission result = permissionService.buscarPorId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIdPermission());
    }

    @Test
    void buscarPermissionPorId_WhenNoExiste_LanzaExcepcion() {
        // Arrange
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> permissionService.buscarPorId(1));
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void actualizarPermission_WhenExiste_ActualizaCorrectamente() {
        // Arrange
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permission));
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission actualizado = new Permission();
        actualizado.setNombre("EDITAR_USUARIO");
        actualizado.setDescripcion("Permite editar usuarios");

        // Act
        Permission result = permissionService.actualizar(1, actualizado);

        // Assert
        assertEquals("EDITAR_USUARIO", result.getNombre());
    }

    @Test
    void actualizarPermission_WhenNoExiste_LanzaExcepcion() {
        // Arrange
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> permissionService.actualizar(1, permission));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void eliminarPermission_WhenExiste_EliminaCorrectamente() {
        // Arrange
        doNothing().when(permissionRepository).deleteById(1);

        // Act
        permissionService.eliminar(1);

        // Assert
        verify(permissionRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarPermission_WhenFalla_LanzaExcepcion() {
        // Arrange
        doThrow(new RuntimeException()).when(permissionRepository).deleteById(1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> permissionService.eliminar(1));
    }
}