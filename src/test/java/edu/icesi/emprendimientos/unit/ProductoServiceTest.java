package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.repository.ProductoRepository;
import edu.icesi.emprendimientos.service.impl.ProductoServiceImpl;
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
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;
    private Emprendimiento emprendimiento;
    private Estado estado;

    @BeforeEach
    void setup() {
        emprendimiento = new Emprendimiento();
        estado = new Estado();
        
        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Producto Test");
        producto.setPrecio(BigDecimal.valueOf(100));
        producto.setStockDisponible(10);
        producto.setEmprendimiento(emprendimiento);
        producto.setEstado(estado);
    }

    @Test
    void guardarProducto_Valido_SeGuardaCorrectamente() {
        when(productoRepository.save(producto)).thenReturn(producto);
        Producto result = productoService.guardar(producto);
        assertNotNull(result);
        assertEquals("Producto Test", result.getNombre());
    }

    @Test
    void guardarProducto_SinNombre_LanzaExcepcion() {
        producto.setNombre(null);
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
    }

    @Test
    void guardarProducto_SinPrecio_LanzaExcepcion() {
        producto.setPrecio(null);
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
    }

    @Test
    void guardarProducto_PrecioNegativo_LanzaExcepcion() {
        producto.setPrecio(java.math.BigDecimal.valueOf(-10));
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
    }

    @Test
    void guardarProducto_StockNegativo_LanzaExcepcion() {
        producto.setStockDisponible(-5);
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
    }

    @Test
    void guardarProducto_SinEmprendimiento_LanzaExcepcion() {
        producto.setEmprendimiento(null);
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
    }

    @Test
    void guardarProducto_SinEstado_LanzaExcepcion() {
        producto.setEstado(null);
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
    }

    @Test
    void listarProductos_ReturnLista() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));
        List<Producto> result = productoService.listar();
        assertEquals(1, result.size());
    }

    @Test
    void buscarProductoPorId_Existe_ReturnProducto() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        Producto result = productoService.buscarPorId(1);
        assertNotNull(result);
        assertEquals(1, result.getIdProducto());
    }

    @Test
    void buscarProductoPorId_NoExiste_LanzaExcepcion() {
        when(productoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productoService.buscarPorId(1));
    }

    @Test
    void actualizarProducto_Existe_ActualizaCorrectamente() {
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        Producto actualizado = new Producto();
        actualizado.setNombre("Nuevo Nombre");
        actualizado.setPrecio(BigDecimal.valueOf(200));
        actualizado.setStockDisponible(20);
        Producto result = productoService.actualizar(1, actualizado);
        assertEquals("Nuevo Nombre", result.getNombre());
    }

    @Test
    void eliminarProducto_Existe_EliminaCorrectamente() {
        doNothing().when(productoRepository).deleteById(1);
        productoService.eliminar(1);
        verify(productoRepository, times(1)).deleteById(1);
    }

    @Test
    void listarPorEmprendimiento_ReturnLista() {
        when(productoRepository.findByEmprendimiento_IdEmprendimiento(1)).thenReturn(Arrays.asList(producto));
        List<Producto> result = productoService.listarPorEmprendimiento(1);
        assertEquals(1, result.size());
    }
}
