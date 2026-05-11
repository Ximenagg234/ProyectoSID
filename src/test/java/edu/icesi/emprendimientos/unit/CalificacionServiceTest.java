package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Calificacion;
import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.repository.CalificacionRepository;
import edu.icesi.emprendimientos.service.CalificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalificacionServiceTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @InjectMocks
    private CalificacionService calificacionService;

    private Emprendimiento emprendimiento;
    private Pedido pedido;
    private Calificacion calificacion1;
    private Calificacion calificacion2;

    @BeforeEach
    void setup() {
        emprendimiento = new Emprendimiento();
        emprendimiento.setIdEmprendimiento(1);

        Emprendimiento emprendimiento2 = new Emprendimiento();
        emprendimiento2.setIdEmprendimiento(2);

        pedido = new Pedido();
        pedido.setIdPedido(10);

        calificacion1 = new Calificacion();
        calificacion1.setIdCalificacion(1);
        calificacion1.setPuntuacion(4);
        calificacion1.setComentario("Muy bueno");
        calificacion1.setFecha(new Date());
        calificacion1.setEmprendimiento(emprendimiento);
        calificacion1.setPedido(pedido);

        calificacion2 = new Calificacion();
        calificacion2.setIdCalificacion(2);
        calificacion2.setPuntuacion(5);
        calificacion2.setComentario("Excelente");
        calificacion2.setFecha(new Date());
        calificacion2.setEmprendimiento(emprendimiento);

        Pedido otroPedido = new Pedido();
        otroPedido.setIdPedido(20);
        calificacion2.setPedido(otroPedido);

        // calificacion de OTRO emprendimiento
        Calificacion calificacionOtra = new Calificacion();
        calificacionOtra.setIdCalificacion(3);
        calificacionOtra.setPuntuacion(3);
        calificacionOtra.setEmprendimiento(emprendimiento2);
    }

    // ─── listarPorEmprendimiento ───────────────────────────────────────────────

    @Test
    void listarPorEmprendimiento_ConCalificaciones_RetornaLista() {
        when(calificacionRepository.findAll())
                .thenReturn(Arrays.asList(calificacion1, calificacion2));

        List<Calificacion> result = calificacionService.listarPorEmprendimiento(1);
        assertEquals(2, result.size());
    }

    @Test
    void listarPorEmprendimiento_SinCalificaciones_RetornaListaVacia() {
        when(calificacionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Calificacion> result = calificacionService.listarPorEmprendimiento(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void listarPorEmprendimiento_FiltrarPorId_SoloRetornaDelEmprendimiento() {
        Emprendimiento emp2 = new Emprendimiento();
        emp2.setIdEmprendimiento(99);
        Calificacion calAjena = new Calificacion();
        calAjena.setEmprendimiento(emp2);
        calAjena.setPuntuacion(2);

        when(calificacionRepository.findAll())
                .thenReturn(Arrays.asList(calificacion1, calAjena));

        List<Calificacion> result = calificacionService.listarPorEmprendimiento(1);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getEmprendimiento().getIdEmprendimiento());
    }

    // ─── promedioPorEmprendimiento ────────────────────────────────────────────

    @Test
    void promedioPorEmprendimiento_ConCalificaciones_CalculaPromedio() {
        when(calificacionRepository.findAll())
                .thenReturn(Arrays.asList(calificacion1, calificacion2));
        // calificacion1.puntuacion=4, calificacion2.puntuacion=5 → promedio=4.5
        Double promedio = calificacionService.promedioPorEmprendimiento(1);
        assertEquals(4.5, promedio, 0.01);
    }

    @Test
    void promedioPorEmprendimiento_SinCalificaciones_RetornaCero() {
        when(calificacionRepository.findAll()).thenReturn(Collections.emptyList());

        Double promedio = calificacionService.promedioPorEmprendimiento(1);
        assertEquals(0.0, promedio);
    }

    @Test
    void promedioPorEmprendimiento_UnaCalificacion_RetornaPuntuacion() {
        when(calificacionRepository.findAll())
                .thenReturn(Collections.singletonList(calificacion1));

        Double promedio = calificacionService.promedioPorEmprendimiento(1);
        assertEquals(4.0, promedio, 0.01);
    }

    // ─── yaCalifico ───────────────────────────────────────────────────────────

    @Test
    void yaCalifico_PedidoConCalificacion_RetornaTrue() {
        when(calificacionRepository.findAll())
                .thenReturn(Collections.singletonList(calificacion1));
        // calificacion1.pedido.idPedido = 10
        assertTrue(calificacionService.yaCalifico(10));
    }

    @Test
    void yaCalifico_PedidoSinCalificacion_RetornaFalse() {
        when(calificacionRepository.findAll())
                .thenReturn(Collections.singletonList(calificacion1));

        assertFalse(calificacionService.yaCalifico(99));
    }

    @Test
    void yaCalifico_ListaVacia_RetornaFalse() {
        when(calificacionRepository.findAll()).thenReturn(Collections.emptyList());

        assertFalse(calificacionService.yaCalifico(10));
    }

    // ─── guardar ──────────────────────────────────────────────────────────────

    @Test
    void guardar_CalificacionValida_RetornaCalificacionGuardada() {
        when(calificacionRepository.save(calificacion1)).thenReturn(calificacion1);

        Calificacion result = calificacionService.guardar(calificacion1);
        assertNotNull(result);
        assertEquals(4, result.getPuntuacion());
        verify(calificacionRepository, times(1)).save(calificacion1);
    }
}
