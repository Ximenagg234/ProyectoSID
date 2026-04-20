package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.service.impl.EstadoServiceImpl;
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
public class EstadoServiceTest {

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private EstadoServiceImpl estadoService;

    private Estado estado;

    @BeforeEach
    void setup() {
        estado = new Estado();
        estado.setIdEstado(1);
        estado.setNombre("Activo");
    }

    @Test
    void listarEstados_ReturnLista() {
        when(estadoRepository.findAll()).thenReturn(Arrays.asList(estado));
        List<Estado> result = estadoService.listar();
        assertEquals(1, result.size());
    }

    @Test
    void listarEstados_ListaVacia_ReturnListaVacia() {
        when(estadoRepository.findAll()).thenReturn(Arrays.asList());
        List<Estado> result = estadoService.listar();
        assertTrue(result.isEmpty());
    }

    @Test
    void buscarEstadoPorId_Existe_ReturnEstado() {
        when(estadoRepository.findById(1)).thenReturn(Optional.of(estado));
        Estado result = estadoService.buscarPorId(1);
        assertNotNull(result);
        assertEquals(1, result.getIdEstado());
        assertEquals("Activo", result.getNombre());
    }

    @Test
    void buscarEstadoPorId_NoExiste_LanzaExcepcion() {
        when(estadoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> estadoService.buscarPorId(1));
    }

    @Test
    void buscarEstadoPorId_ConIdNegativo_LanzaExcepcion() {
        when(estadoRepository.findById(-1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> estadoService.buscarPorId(-1));
    }
}

