package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Estado;

import java.util.List;

public interface EstadoService {

    List<Estado> listar();

    Estado buscarPorId(Integer id);
}