package edu.icesi.emprendimientos.unit;

import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.rest.dto.*;
import edu.icesi.emprendimientos.rest.mapper.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MapperTest {

    @Autowired
    private CategoriaMapper categoriaMapper;

    @Autowired
    private RolMapper rolMapper;

    @Autowired
    private EmprendimientoMapper emprendimientoMapper;

    @Autowired
    private ProductoMapper productoMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private PedidoMapper pedidoMapper;

    // ─── CategoriaMapper ──────────────────────────────────────────────────────

    @Test
    void categoriaMapper_toDto_MappeaCorrectamente() {
        Categoria cat = new Categoria();
        cat.setIdCategoria(1);
        cat.setNombre("Tecnologia");
        cat.setDescripcion("Productos tecnologicos");

        CategoriaResponseDTO dto = categoriaMapper.toDto(cat);

        assertNotNull(dto);
        assertEquals(1, dto.getIdCategoria());
        assertEquals("Tecnologia", dto.getNombre());
        assertEquals("Productos tecnologicos", dto.getDescripcion());
    }

    @Test
    void categoriaMapper_toEntity_MappeaCamposYIgnoraId() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Moda", "Ropa y accesorios");

        Categoria entity = categoriaMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getIdCategoria());      // ignored
        assertNull(entity.getEmprendimientos());  // ignored
        assertEquals("Moda", entity.getNombre());
        assertEquals("Ropa y accesorios", entity.getDescripcion());
    }

    // ─── RolMapper ────────────────────────────────────────────────────────────

    @Test
    void rolMapper_toDto_MappeaCorrectamente() {
        Rol rol = new Rol();
        rol.setIdRol(2);
        rol.setNombre("EMPRENDEDOR");

        RolResponseDTO dto = rolMapper.toDto(rol);

        assertNotNull(dto);
        assertEquals(2, dto.getIdRol());
        assertEquals("EMPRENDEDOR", dto.getNombre());
    }

    @Test
    void rolMapper_toEntity_MappeaCamposYIgnoraRelaciones() {
        RolRequestDTO dto = new RolRequestDTO("COMPRADOR");

        Rol entity = rolMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getIdRol());      // ignored
        assertNull(entity.getPermisos());   // ignored
        assertNull(entity.getUsuarios());   // ignored
        assertEquals("COMPRADOR", entity.getNombre());
    }

    // ─── EmprendimientoMapper ─────────────────────────────────────────────────

    @Test
    void emprendimientoMapper_toDto_MappeaCamposRelacionados() {
        Emprendimiento emp = new Emprendimiento();
        emp.setIdEmprendimiento(1);
        emp.setNombre("TechStore");
        emp.setDescripcion("Accesorios tech");
        emp.setLogoUrl("http://logo.png");

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto("Ximena Gomez");
        emp.setUsuario(usuario);

        Categoria cat = new Categoria();
        cat.setNombre("Tecnologia");
        emp.setCategoria(cat);

        Semestre semestre = new Semestre();
        semestre.setPeriodo("2026-1");
        emp.setSemestre(semestre);

        Estado estado = new Estado();
        estado.setNombre("ACTIVO");
        emp.setEstado(estado);

        EmprendimientoResponseDTO dto = emprendimientoMapper.toDto(emp);

        assertNotNull(dto);
        assertEquals(1, dto.getIdEmprendimiento());
        assertEquals("TechStore", dto.getNombre());
        assertEquals("Ximena Gomez", dto.getNombreUsuario());
        assertEquals("Tecnologia", dto.getNombreCategoria());
        assertEquals("2026-1", dto.getNombreSemestre());
        assertEquals("ACTIVO", dto.getNombreEstado());
    }

    @Test
    void emprendimientoMapper_toEntity_IgnoraRelacionesYId() {
        EmprendimientoRequestDTO dto = new EmprendimientoRequestDTO(
                "NuevoEmp", "Descripcion", "http://logo.png", 1, 1, 1, 1);

        Emprendimiento entity = emprendimientoMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getIdEmprendimiento()); // ignored
        assertNull(entity.getUsuario());          // ignored
        assertNull(entity.getCategoria());        // ignored
        assertEquals("NuevoEmp", entity.getNombre());
        assertEquals("Descripcion", entity.getDescripcion());
    }

    // ─── ProductoMapper ───────────────────────────────────────────────────────

    @Test
    void productoMapper_toDto_MappeaNombreEmprendimientoYEstado() {
        Producto prod = new Producto();
        prod.setIdProducto(5);
        prod.setNombre("Audifonos");
        prod.setPrecio(BigDecimal.valueOf(150000));

        Emprendimiento emp = new Emprendimiento();
        emp.setNombre("TechStore");
        prod.setEmprendimiento(emp);

        Estado estado = new Estado();
        estado.setNombre("ACTIVO");
        prod.setEstado(estado);

        ProductoResponseDTO dto = productoMapper.toDto(prod);

        assertNotNull(dto);
        assertEquals("Audifonos", dto.getNombre());
        assertEquals("TechStore", dto.getNombreEmprendimiento());
        assertEquals("ACTIVO", dto.getNombreEstado());
    }

    @Test
    void productoMapper_toEntity_IgnoraRelaciones() {
        ProductoRequestDTO dto = new ProductoRequestDTO(
                "Teclado", "Teclado mec", BigDecimal.valueOf(80000), 10, 1, 1);

        Producto entity = productoMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getIdProducto());     // ignored
        assertNull(entity.getEmprendimiento()); // ignored
        assertNull(entity.getEstado());         // ignored
        assertEquals("Teclado", entity.getNombre());
        assertEquals(0, BigDecimal.valueOf(80000).compareTo(entity.getPrecio()));
    }

    // ─── UsuarioMapper ────────────────────────────────────────────────────────

    @Test
    void usuarioMapper_toDto_ConRoles_MappeaListaRoles() {
        Rol rol = new Rol();
        rol.setNombre("ADMIN");

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setRol(rol);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombreCompleto("Ximena Gomez");
        usuario.setCorreoInstitucional("ximena@icesi.edu.co");
        usuario.setRoles(Collections.singletonList(usuarioRol));

        UsuarioResponseDTO dto = usuarioMapper.toDto(usuario);

        assertNotNull(dto);
        assertEquals("Ximena Gomez", dto.getNombreCompleto());
        assertNotNull(dto.getRoles());
        assertEquals(1, dto.getRoles().size());
        assertEquals("ADMIN", dto.getRoles().get(0));
    }

    @Test
    void usuarioMapper_toDto_SinRoles_RetornaListaVacia() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2);
        usuario.setNombreCompleto("Carlos Perez");
        usuario.setRoles(null);

        UsuarioResponseDTO dto = usuarioMapper.toDto(usuario);

        assertNotNull(dto);
        assertNotNull(dto.getRoles());
        assertTrue(dto.getRoles().isEmpty());
    }

    @Test
    void usuarioMapper_toEntity_IgnoraIdYRoles() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Nuevo Usuario", "nuevo@icesi.edu.co",
                "Ingenieria de Sistemas", 5, null, "pass123");

        Usuario entity = usuarioMapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getIdUsuario()); // ignored
        assertNull(entity.getRoles());     // ignored
        assertEquals("Nuevo Usuario", entity.getNombreCompleto());
        assertEquals("nuevo@icesi.edu.co", entity.getCorreoInstitucional());
    }
}
