package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.repository.RolRepository;
import edu.icesi.emprendimientos.service.RolService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;

    public RolServiceImpl(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public Rol guardar(Rol rol) {

        if (rol.getPermisos() == null || rol.getPermisos().isEmpty()) {
            throw new RuntimeException("El rol debe tener al menos un permiso");
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
}