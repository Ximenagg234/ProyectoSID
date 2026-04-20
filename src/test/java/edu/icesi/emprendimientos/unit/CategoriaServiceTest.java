package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.Categoria;
import edu.icesi.emprendimientos.repository.CategoriaRepository;
import edu.icesi.emprendimientos.service.impl.CategoriaServiceImpl;
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
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setup() {
        categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");
    }

    @Test
    void guardarCategoria_Valida_SeGuardaCorrectamente() {
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        Categoria result = categoriaService.guardar(categoria);
        assertNotNull(result);
        assertEquals("Electrónica", result.getNombre());
    }

    @Test
    void guardarCategoria_SinNombre_LanzaExcepcion() {
        categoria.setNombre(null);
        assertThrows(RuntimeException.class, () -> categoriaService.guardar(categoria));
    }

    @Test
    void guardarCategoria_NombreVacio_LanzaExcepcion() {
        categoria.setNombre("");
        assertThrows(RuntimeException.class, () -> categoriaService.guardar(categoria));
    }

    @Test
    void listarCategorias_ReturnLista() {
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria));
        List<Categoria> result = categoriaService.listar();
        assertEquals(1, result.size());
    }

    @Test
    void buscarCategoriaPorId_Existe_ReturnCategoria() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        Categoria result = categoriaService.buscarPorId(1);
        assertNotNull(result);
        assertEquals(1, result.getIdCategoria());
    }

    @Test
    void buscarCategoriaPorId_NoExiste_LanzaExcepcion() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoriaService.buscarPorId(1));
    }

    @Test
    void actualizarCategoria_Existe_ActualizaCorrectamente() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        Categoria actualizada = new Categoria();
        actualizada.setNombre("Ropa");
        actualizada.setDescripcion("Prendas de vestir");
        Categoria result = categoriaService.actualizar(1, actualizada);
        assertEquals("Ropa", result.getNombre());
    }

    @Test
    void actualizarCategoria_NoExiste_LanzaExcepcion() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoriaService.actualizar(1, categoria));
    }

    @Test
    void eliminarCategoria_Existe_EliminaCorrectamente() {
        doNothing().when(categoriaRepository).deleteById(1);
        categoriaService.eliminar(1);
        verify(categoriaRepository, times(1)).deleteById(1);
    }
}

