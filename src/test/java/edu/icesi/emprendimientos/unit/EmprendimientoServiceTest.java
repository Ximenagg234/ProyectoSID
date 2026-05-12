package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.entity.Categoria;
import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.repository.EmprendimientoRepository;
import edu.icesi.emprendimientos.service.impl.EmprendimientoServiceImpl;
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
public class EmprendimientoServiceTest {

    @Mock
    private EmprendimientoRepository emprendimientoRepository;

    @InjectMocks
    private EmprendimientoServiceImpl emprendimientoService;

    private Emprendimiento emprendimiento;
    private Usuario usuario;
    private Categoria categoria;
    private Estado estado;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        categoria = new Categoria();
        estado = new Estado();
        
        emprendimiento = new Emprendimiento();
        emprendimiento.setIdEmprendimiento(1);
        emprendimiento.setNombre("Mi Emprendimiento");
        emprendimiento.setDescripcion("Descripción del emprendimiento");
        emprendimiento.setUsuario(usuario);
        emprendimiento.setCategoria(categoria);
        emprendimiento.setEstado(estado);
    }

    @Test
    void guardarEmprendimiento_Valido_SeGuardaCorrectamente() {
        when(emprendimientoRepository.save(any(Emprendimiento.class))).thenReturn(emprendimiento);
        Emprendimiento result = emprendimientoService.guardar(emprendimiento);
        assertNotNull(result);
        assertEquals("Mi Emprendimiento", result.getNombre());
    }

    @Test
    void guardarEmprendimiento_SinNombre_LanzaExcepcion() {
        emprendimiento.setNombre(null);
        assertThrows(RuntimeException.class, () -> emprendimientoService.guardar(emprendimiento));
    }

    @Test
    void guardarEmprendimiento_SinUsuario_LanzaExcepcion() {
        emprendimiento.setUsuario(null);
        assertThrows(RuntimeException.class, () -> emprendimientoService.guardar(emprendimiento));
    }

    @Test
    void guardarEmprendimiento_SinCategoria_LanzaExcepcion() {
        emprendimiento.setCategoria(null);
        assertThrows(RuntimeException.class, () -> emprendimientoService.guardar(emprendimiento));
    }

    @Test
    void guardarEmprendimiento_SinEstado_LanzaExcepcion() {
        emprendimiento.setEstado(null);
        assertThrows(RuntimeException.class, () -> emprendimientoService.guardar(emprendimiento));
    }

    @Test
    void listarEmprendimientos_ReturnLista() {
        when(emprendimientoRepository.findAll()).thenReturn(Arrays.asList(emprendimiento));
        List<Emprendimiento> result = emprendimientoService.listar();
        assertEquals(1, result.size());
    }

    @Test
    void buscarEmprendimientoPorId_Existe_ReturnEmprendimiento() {
        when(emprendimientoRepository.findById(1)).thenReturn(Optional.of(emprendimiento));
        Emprendimiento result = emprendimientoService.buscarPorId(1);
        assertNotNull(result);
        assertEquals(1, result.getIdEmprendimiento());
    }

    @Test
    void buscarEmprendimientoPorId_NoExiste_LanzaExcepcion() {
        when(emprendimientoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> emprendimientoService.buscarPorId(1));
    }

    @Test
    void actualizarEmprendimiento_Existe_ActualizaCorrectamente() {
        when(emprendimientoRepository.findById(1)).thenReturn(Optional.of(emprendimiento));
        when(emprendimientoRepository.save(any(Emprendimiento.class))).thenReturn(emprendimiento);
        Emprendimiento actualizado = new Emprendimiento();
        actualizado.setNombre("Nuevo Nombre");
        actualizado.setDescripcion("Nueva descripción");
        actualizado.setLogoUrl("http://logo.png");
        Emprendimiento result = emprendimientoService.actualizar(1, actualizado);
        assertEquals("Nuevo Nombre", result.getNombre());
    }

    @Test
    void eliminarEmprendimiento_Existe_EliminaCorrectamente() {
        doNothing().when(emprendimientoRepository).deleteById(1);
        emprendimientoService.eliminar(1);
        verify(emprendimientoRepository, times(1)).deleteById(1);
    }
}
