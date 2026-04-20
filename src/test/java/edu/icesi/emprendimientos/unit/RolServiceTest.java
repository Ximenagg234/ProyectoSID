package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Permission;
import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.RolPermission;
import edu.icesi.emprendimientos.entity.keys.RolPermissionId;
import edu.icesi.emprendimientos.repository.PermissionRepository;
import edu.icesi.emprendimientos.repository.RolPermissionRepository;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.service.impl.RolServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Mock
    private RolPermissionRepository rolPermissionRepository;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rol;

    @BeforeEach
    void setup() {
        rol = new Rol();
        rol.setIdRol(1);
        rol.setNombre("ADMIN");
        rol.setPermisos(new ArrayList<>(Arrays.asList(new RolPermission())));
    }

    // =========================
    // CREATE
    // =========================

    @Test
    void guardarRol_WhenTienePermisos_SeGuardaCorrectamente() {
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol result = rolService.guardar(rol);

        assertNotNull(result);
        assertEquals("ADMIN", result.getNombre());
    }

    @Test
    void guardarRol_WhenNoTienePermisos_SeGuardaConListaVacia() {
        // guardar() ya no lanza excepción si permisos es null — inicializa lista vacía
        rol.setPermisos(null);
        when(rolRepository.save(rol)).thenReturn(rol);

        Rol result = rolService.guardar(rol);

        assertNotNull(result);
        assertNotNull(result.getPermisos()); // se inicializó la lista
    }

    // =========================
    // READ
    // =========================

    @Test
    void listarRoles_ReturnListaRoles() {
        when(rolRepository.findAll()).thenReturn(Arrays.asList(rol));

        List<Rol> result = rolService.listar();

        assertEquals(1, result.size());
    }

    @Test
    void buscarRolPorId_WhenExiste_ReturnRol() {
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        Rol result = rolService.buscarPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdRol());
    }

    @Test
    void buscarRolPorId_WhenNoExiste_LanzaExcepcion() {
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rolService.buscarPorId(1));
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void actualizarRol_WhenExiste_ActualizaCorrectamente() {
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol actualizado = new Rol();
        actualizado.setNombre("CLIENTE");

        Rol result = rolService.actualizar(1, actualizado);

        assertEquals("CLIENTE", result.getNombre());
    }

    @Test
    void actualizarRol_WhenNoExiste_LanzaExcepcion() {
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rolService.actualizar(1, rol));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void eliminarRol_WhenExiste_SeEliminaCorrectamente() {
        doNothing().when(rolRepository).deleteById(1);

        rolService.eliminar(1);

        verify(rolRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarRol_WhenFalla_LanzaExcepcion() {
        doThrow(new RuntimeException()).when(rolRepository).deleteById(1);

        assertThrows(RuntimeException.class, () -> rolService.eliminar(1));
    }

    // =========================
    // ASIGNAR PERMISO
    // =========================

    @Test
    void asignarPermiso_RolExiste_PermisoExiste_AsignaCorrectamente() {
        RolPermissionId id = new RolPermissionId(1, 1);
        Permission permiso = new Permission();
        permiso.setIdPermission(1);

        when(rolPermissionRepository.existsById(id)).thenReturn(false);
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permiso));
        when(rolPermissionRepository.save(any(RolPermission.class))).thenReturn(new RolPermission());

        rolService.asignarPermiso(1, 1);

        verify(rolPermissionRepository, times(1)).save(any(RolPermission.class));
    }

    @Test
    void asignarPermiso_YaAsignado_NoGuardaDuplicado() {
        RolPermissionId id = new RolPermissionId(1, 1);
        when(rolPermissionRepository.existsById(id)).thenReturn(true);

        rolService.asignarPermiso(1, 1);

        verify(rolPermissionRepository, never()).save(any());
    }

    @Test
    void asignarPermiso_RolNoExiste_LanzaExcepcion() {
        RolPermissionId id = new RolPermissionId(1, 1);
        when(rolPermissionRepository.existsById(id)).thenReturn(false);
        when(rolRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rolService.asignarPermiso(1, 1));
    }

    @Test
    void asignarPermiso_PermisoNoExiste_LanzaExcepcion() {
        RolPermissionId id = new RolPermissionId(1, 1);
        when(rolPermissionRepository.existsById(id)).thenReturn(false);
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(permissionRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rolService.asignarPermiso(1, 1));
    }

    // =========================
    // QUITAR PERMISO
    // =========================

    @Test
    void quitarPermiso_QuitaCorrectamente() {
        RolPermissionId id = new RolPermissionId(1, 1);
        doNothing().when(rolPermissionRepository).deleteById(id);

        rolService.quitarPermiso(1, 1);

        verify(rolPermissionRepository, times(1)).deleteById(id);
    }

    @Test
    void quitarPermiso_WhenFalla_LanzaExcepcion() {
        RolPermissionId id = new RolPermissionId(1, 1);
        doThrow(new RuntimeException()).when(rolPermissionRepository).deleteById(id);

        assertThrows(RuntimeException.class, () -> rolService.quitarPermiso(1, 1));
    }
}
