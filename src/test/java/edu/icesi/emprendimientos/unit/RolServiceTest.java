package edu.icesi.emprendimientos.unit;


import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.RolPermission;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.service.impl.RolServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rol;
    private RolPermission rolPermission;

    @BeforeEach
    void setup() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ADMIN");

        rolPermission = new RolPermission();

        rol.setPermisos(Arrays.asList(rolPermission));
    }

    // =========================
    // CREATE (guardar)
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
    // READ (listar)
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
}
