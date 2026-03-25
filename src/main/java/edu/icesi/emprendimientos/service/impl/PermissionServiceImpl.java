package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Permission;
import edu.icesi.emprendimientos.repository.PermissionRepository;
import edu.icesi.emprendimientos.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission guardar(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> listar() {
        return permissionRepository.findAll();
    }
}