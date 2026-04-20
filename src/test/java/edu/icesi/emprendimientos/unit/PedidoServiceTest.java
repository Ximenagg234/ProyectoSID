package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.repository.PedidoRepository;
import edu.icesi.emprendimientos.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedido;
    private DetallePedido detallePedido;

    @BeforeEach
    void setup() {
        detallePedido = new DetallePedido();
        detallePedido.setSubtotal(BigDecimal.valueOf(100));

        pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setDetalles(Arrays.asList(detallePedido));
    }

    @Test
    void crearPedido_Valido_SeGuardaCorrectamente() {
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        Pedido result = pedidoService.crearPedido(pedido);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100), result.getTotal());
    }

    @Test
    void crearPedido_SinDetalles_LanzaExcepcion() {
        pedido.setDetalles(null);
        assertThrows(RuntimeException.class, () -> pedidoService.crearPedido(pedido));
    }

    @Test
    void crearPedido_DetallesVacio_LanzaExcepcion() {
        pedido.setDetalles(Arrays.asList());
        assertThrows(RuntimeException.class, () -> pedidoService.crearPedido(pedido));
    }

    @Test
    void crearPedido_CalculaTotalCorrectamente() {
        DetallePedido detalle2 = new DetallePedido();
        detalle2.setSubtotal(BigDecimal.valueOf(150));
        pedido.setDetalles(Arrays.asList(detallePedido, detalle2));

        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        Pedido result = pedidoService.crearPedido(pedido);

        assertEquals(BigDecimal.valueOf(250), result.getTotal());
    }

    @Test
    void crearPedido_EstableceBidireccionalidad() {
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        pedidoService.crearPedido(pedido);
        assertEquals(pedido, detallePedido.getPedido());
    }

    @Test
    void listarPedidos_ReturnLista() {
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido));
        List<Pedido> result = pedidoService.listar();
        assertEquals(1, result.size());
    }

    @Test
    void buscarPedidoPorId_Existe_ReturnPedido() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        Pedido result = pedidoService.buscarPorId(1);
        assertNotNull(result);
        assertEquals(1, result.getIdPedido());
    }

    @Test
    void buscarPedidoPorId_NoExiste_LanzaExcepcion() {
        when(pedidoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> pedidoService.buscarPorId(1));
    }

    @Test
    void listarPorUsuario_ReturnLista() {
        when(pedidoRepository.findByUsuario_IdUsuario(1)).thenReturn(Arrays.asList(pedido));
        List<Pedido> result = pedidoService.listarPorUsuario(1);
        assertEquals(1, result.size());
    }

    @Test
    void eliminarPedido_Existe_EliminaCorrectamente() {
        doNothing().when(pedidoRepository).deleteById(1);
        pedidoService.eliminar(1);
        verify(pedidoRepository, times(1)).deleteById(1);
    }
}

