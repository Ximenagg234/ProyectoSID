package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Rol;

import java.util.List;

public interface RolService {

    Rol guardar(Rol rol);

    List<Rol> listar();
}