package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    Usuario guardar(Usuario usuario);
    List<Usuario> listar();
    void eliminar(Integer id);
    Usuario buscarPorId(Integer id);
    Usuario actualizar(Integer id, Usuario usuarioActualizado);
    void asignarRol(Integer idUsuario, Integer idRol);
    void quitarRol(Integer idUsuario, Integer idRol);
}