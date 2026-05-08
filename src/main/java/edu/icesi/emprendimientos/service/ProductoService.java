package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Producto;

import java.util.List;

public interface ProductoService {

    Producto guardar(Producto producto);

    List<Producto> listar();

    Producto buscarPorId(Integer id);

    Producto actualizar(Integer id, Producto producto);

    void eliminar(Integer id);

    List<Producto> listarPorEmprendimiento(Integer idEmprendimiento);

    List<Producto> listarPorCategoria(Integer idCategoria);

    List<Producto> listarActivos();

    List<Producto> listarActivosPorCategoria(Integer idCategoria);
}
