package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Categoria;

import java.util.List;

public interface CategoriaService {

    Categoria guardar(Categoria categoria);

    List<Categoria> listar();

    Categoria buscarPorId(Integer id);

    Categoria actualizar(Integer id, Categoria categoria);

    void eliminar(Integer id);
}