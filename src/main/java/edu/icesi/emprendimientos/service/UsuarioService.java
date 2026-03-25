package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario guardar(Usuario usuario);

    List<Usuario> listar();

    void eliminar(Integer id);
}