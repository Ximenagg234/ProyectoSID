package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.RolPermission;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.repository.PermissionRepository;
import edu.icesi.emprendimientos.service.impl.RolServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rol;

    @BeforeEach
    void setup() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ADMIN");

        RolPermission rolPermission = new RolPermission();
        rol.setPermisos(new java.util.ArrayList<>(Arrays.asList(rolPermission)));
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void guardarRol_WhenTienePermisos_SeGuardaCorrectamente() {
        // Arrange
        when(rolRepository.save(rol)).thenReturn(rol);

        // Act
        Rol result = rolService.guardar(rol);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getNombre());
    }

    @Test
    void guardarRol_WhenNoTienePermisos_LanzaExcepcion() {
        // Arrange
        rol.setPermisos(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rolService.guardar(rol));
    }

    // =========================
    // READ
    // =========================

    @Test
    void listarRoles_ReturnListaRoles() {
        // Arrange
        when(rolRepository.findAll()).thenReturn(Arrays.asList(rol));

        // Act
        List<Rol> result = rolService.listar();

        // Assert
        assertEquals(1, result.size());
    }

    // =========================
    // READ
    // =========================

    @Test
    void buscarRolPorId_WhenExiste_ReturnRol() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        // Act
        Rol result = rolService.buscarPorId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIdRol());
    }

    @Test
    void buscarRolPorId_WhenNoExiste_LanzaExcepcion() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rolService.buscarPorId(1));
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void actualizarRol_WhenExiste_ActualizaCorrectamente() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol actualizado = new Rol();
        actualizado.setNombre("CLIENTE");

        // Act
        Rol result = rolService.actualizar(1, actualizado);

        // Assert
        assertEquals("CLIENTE", result.getNombre());
    }

    @Test
    void actualizarRol_WhenNoExiste_LanzaExcepcion() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rolService.actualizar(1, rol));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void eliminarRol_WhenExiste_SeEliminaCorrectamente() {
        // Arrange
        doNothing().when(rolRepository).deleteById(1);

        // Act
        rolService.eliminar(1);

        // Assert
        verify(rolRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarRol_WhenFalla_LanzaExcepcion() {
        // Arrange
        doThrow(new RuntimeException()).when(rolRepository).deleteById(1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> rolService.eliminar(1));
    }

    // =========================
    // PERMISOS
    // =========================

    @Test
    void asignarPermiso_RolExiste_PermisoExiste_AsignaCorrectamente() {
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(permissionRepository.findById(1)).thenReturn(Optional.of(new edu.icesi.emprendimientos.entity.Permission()));
        when(rolRepository.save(any(edu.icesi.emprendimientos.entity.Rol.class))).thenReturn(rol);
        
        rolService.asignarPermiso(1, 1);
        
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void asignarPermiso_RolNoExiste_LanzaExcepcion() {
        when(rolRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> rolService.asignarPermiso(1, 1));
    }

    @Test
    void asignarPermiso_PermisoNoExiste_LanzaExcepcion() {
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> rolService.asignarPermiso(1, 1));
    }

    @Test
    void quitarPermiso_RolExiste_QuitaCorrectamente() {
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(edu.icesi.emprendimientos.entity.Rol.class))).thenReturn(rol);
        
        rolService.quitarPermiso(1, 1);
        
        verify(rolRepository, times(1)).save(rol);
    }

    @Test
    void quitarPermiso_RolNoExiste_LanzaExcepcion() {
        when(rolRepository.findById(1)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> rolService.quitarPermiso(1, 1));
    }
}