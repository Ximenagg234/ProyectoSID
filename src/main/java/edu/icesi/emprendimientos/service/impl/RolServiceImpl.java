package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Permission;
import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.entity.RolPermission;
import edu.icesi.emprendimientos.entity.keys.RolPermissionId;
import edu.icesi.emprendimientos.repository.PermissionRepository;
import edu.icesi.emprendimientos.repository.RolPermissionRepository;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.service.RolService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final PermissionRepository permissionRepository;
    private final RolPermissionRepository rolPermissionRepository;

    public RolServiceImpl(RolRepository rolRepository,
                          PermissionRepository permissionRepository,
                          RolPermissionRepository rolPermissionRepository) {
        this.rolRepository = rolRepository;
        this.permissionRepository = permissionRepository;
        this.rolPermissionRepository = rolPermissionRepository;
    }

    @Override
    public Rol guardar(Rol rol) {
        if (rol.getPermisos() == null) {
            rol.setPermisos(new ArrayList<>());
        }
        return rolRepository.save(rol);
    }

    @Override
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Override
    public Rol buscarPorId(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    @Override
    public Rol actualizar(Integer id, Rol rolActualizado) {
        Rol existente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        existente.setNombre(rolActualizado.getNombre());
        return rolRepository.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        rolRepository.deleteById(id);
    }

    @Override
    public void asignarPermiso(Integer idRol, Integer idPermission) {
        RolPermissionId id = new RolPermissionId(idRol, idPermission);

        // Si ya existe no hacer nada
        if (rolPermissionRepository.existsById(id)) return;

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Permission permiso = permissionRepository.findById(idPermission)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

        RolPermission rp = new RolPermission();
        rp.setId(id);
        rp.setRol(rol);
        rp.setPermission(permiso);

        // Guardar directamente en la tabla junction, sin cascade
        rolPermissionRepository.save(rp);
    }

    @Override
    public void quitarPermiso(Integer idRol, Integer idPermission) {
        RolPermissionId id = new RolPermissionId(idRol, idPermission);
        rolPermissionRepository.deleteById(id);
    }
}
