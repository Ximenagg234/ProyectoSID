package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.ImagenProducto;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.repository.ImagenProductoRepository;
import edu.icesi.emprendimientos.service.impl.ImagenProductoServiceImpl;
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
public class ImagenProductoServiceTest {

    @Mock
    private ImagenProductoRepository imagenProductoRepository;

    @InjectMocks
    private ImagenProductoServiceImpl imagenProductoService;

    private ImagenProducto imagen;
    private Producto producto;

    @BeforeEach
    void setup() {
        producto = new Producto();
        
        imagen = new ImagenProducto();
        imagen.setUrlImagen("http://ejemplo.com/imagen.jpg");
        imagen.setProducto(producto);
    }

    @Test
    void guardarImagen_Valida_SeGuardaCorrectamente() {
        when(imagenProductoRepository.save(imagen)).thenReturn(imagen);
        ImagenProducto result = imagenProductoService.guardar(imagen);
        assertNotNull(result);
        assertEquals("http://ejemplo.com/imagen.jpg", result.getUrlImagen());
    }

    @Test
    void guardarImagen_SinUrl_LanzaExcepcion() {
        imagen.setUrlImagen(null);
        assertThrows(RuntimeException.class, () -> imagenProductoService.guardar(imagen));
    }

    @Test
    void guardarImagen_UrlVacia_LanzaExcepcion() {
        imagen.setUrlImagen("");
        assertThrows(RuntimeException.class, () -> imagenProductoService.guardar(imagen));
    }

    @Test
    void guardarImagen_SinProducto_LanzaExcepcion() {
        imagen.setProducto(null);
        assertThrows(RuntimeException.class, () -> imagenProductoService.guardar(imagen));
    }

    @Test
    void listarPorProducto_ReturnLista() {
        when(imagenProductoRepository.findByProducto_IdProducto(1)).thenReturn(Arrays.asList(imagen));
        List<ImagenProducto> result = imagenProductoService.listarPorProducto(1);
        assertEquals(1, result.size());
    }

    @Test
    void listarPorProducto_SinImagenes_ReturnListaVacia() {
        when(imagenProductoRepository.findByProducto_IdProducto(1)).thenReturn(Arrays.asList());
        List<ImagenProducto> result = imagenProductoService.listarPorProducto(1);
        assertTrue(result.isEmpty());
    }

    @Test
    void eliminarImagen_Existe_EliminaCorrectamente() {
        doNothing().when(imagenProductoRepository).deleteById(1);
        imagenProductoService.eliminar(1);
        verify(imagenProductoRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarImagen_Falla_LanzaExcepcion() {
        doThrow(new RuntimeException()).when(imagenProductoRepository).deleteById(1);
        assertThrows(RuntimeException.class, () -> imagenProductoService.eliminar(1));
    }
}
