package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.repository.DetallePedidoRepository;
import edu.icesi.emprendimientos.service.impl.DetallePedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetallePedidoServiceTest {

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @InjectMocks
    private DetallePedidoServiceImpl detallePedidoService;

    private DetallePedido detallePedido;
    private Producto producto;

    @BeforeEach
    void setup() {
        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(100));
        producto.setStockDisponible(10);

        detallePedido = new DetallePedido();
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(2);
    }

    @Test
    void crearDetalle_Valido_SeGuardaCorrectamente() {
        when(detallePedidoRepository.save(detallePedido)).thenReturn(detallePedido);
        DetallePedido result = detallePedidoService.crearDetalle(detallePedido);
        assertNotNull(result);
        assertEquals(2, result.getCantidad());
        assertEquals(BigDecimal.valueOf(100), result.getPrecioUnitario());
    }

    @Test
    void crearDetalle_SinProducto_LanzaExcepcion() {
        detallePedido.setProducto(null);
        assertThrows(RuntimeException.class, () -> detallePedidoService.crearDetalle(detallePedido));
    }

    @Test
    void crearDetalle_CantidadCero_LanzaExcepcion() {
        detallePedido.setCantidad(0);
        assertThrows(RuntimeException.class, () -> detallePedidoService.crearDetalle(detallePedido));
    }

    @Test
    void crearDetalle_CantidadNegativa_LanzaExcepcion() {
        detallePedido.setCantidad(-1);
        assertThrows(RuntimeException.class, () -> detallePedidoService.crearDetalle(detallePedido));
    }

    @Test
    void crearDetalle_StockInsuficiente_LanzaExcepcion() {
        detallePedido.setCantidad(15);
        assertThrows(RuntimeException.class, () -> detallePedidoService.crearDetalle(detallePedido));
    }

    @Test
    void crearDetalle_CalculaSubtotalCorrectamente() {
        when(detallePedidoRepository.save(detallePedido)).thenReturn(detallePedido);
        DetallePedido result = detallePedidoService.crearDetalle(detallePedido);
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200), result.getSubtotal());
    }

    @Test
    void crearDetalle_DescontaStockCorrectamente() {
        assertEquals(10, producto.getStockDisponible());
        when(detallePedidoRepository.save(detallePedido)).thenReturn(detallePedido);
        detallePedidoService.crearDetalle(detallePedido);
        assertEquals(8, producto.getStockDisponible());
    }
}

