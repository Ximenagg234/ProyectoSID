package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.repository.EmprendimientoRepository;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final EmprendimientoRepository emprendimientoRepository;

    public EmprendimientoServiceImpl(EmprendimientoRepository emprendimientoRepository) {
        this.emprendimientoRepository = emprendimientoRepository;
    }

    @Override
    public Emprendimiento guardar(Emprendimiento emprendimiento) {
        if (emprendimiento.getNombre() == null || emprendimiento.getNombre().isEmpty())
            throw new RuntimeException("El emprendimiento debe tener nombre");
        if (emprendimiento.getUsuario() == null)
            throw new RuntimeException("El emprendimiento debe tener un usuario");
        if (emprendimiento.getCategoria() == null)
            throw new RuntimeException("El emprendimiento debe tener una categoria");
        if (emprendimiento.getEstado() == null)
            throw new RuntimeException("El emprendimiento debe tener un estado");
        return emprendimientoRepository.save(emprendimiento);
    }

    @Override
    public List<Emprendimiento> listar() {
        return emprendimientoRepository.findAll();
    }

    @Override
    public Emprendimiento buscarPorId(Integer id) {
        return emprendimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado"));
    }

    @Override
    public Emprendimiento actualizar(Integer id, Emprendimiento actualizado) {
        Emprendimiento existente = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emprendimiento no encontrado"));
        existente.setNombre(actualizado.getNombre());
        existente.setDescripcion(actualizado.getDescripcion());
        existente.setLogoUrl(actualizado.getLogoUrl());
        if (actualizado.getCategoria() != null) existente.setCategoria(actualizado.getCategoria());
        return emprendimientoRepository.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        emprendimientoRepository.deleteById(id);
    }

    @Override
    public List<Emprendimiento> listarPorUsuario(Integer idUsuario) {
        return emprendimientoRepository.findByUsuario_IdUsuario(idUsuario);
    }
}
