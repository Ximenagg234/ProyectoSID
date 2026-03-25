package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Permission;

import java.util.List;

public interface PermissionService {

    Permission guardar(Permission permission);

    List<Permission> listar();
}