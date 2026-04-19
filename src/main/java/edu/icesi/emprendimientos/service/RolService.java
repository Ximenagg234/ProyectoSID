package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Rol;

import java.util.List;

public interface RolService {

    Rol guardar(Rol rol);
    List<Rol> listar();
    Rol buscarPorId(Integer id);
    Rol actualizar(Integer id, Rol rolActualizado);
    void eliminar(Integer id);
    void asignarPermiso(Integer idRol, Integer idPermission);
    void quitarPermiso(Integer idRol, Integer idPermission);
}