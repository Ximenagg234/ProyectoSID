package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.mongo.service.MongoSyncService;
import edu.icesi.emprendimientos.repository.EmprendimientoRepository;
import edu.icesi.emprendimientos.service.EmprendimientoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmprendimientoServiceImpl implements EmprendimientoService {

    private final EmprendimientoRepository emprendimientoRepository;
    private final MongoSyncService         mongoSync;

    public EmprendimientoServiceImpl(EmprendimientoRepository emprendimientoRepository,
                                     MongoSyncService mongoSync) {
        this.emprendimientoRepository = emprendimientoRepository;
        this.mongoSync                = mongoSync;
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

        Emprendimiento saved = emprendimientoRepository.save(emprendimiento);

        // Dual-write a MongoDB
        try { mongoSync.sincronizarEmprendimiento(saved); }
        catch (Exception e) { /* no bloquea */ }

        return saved;
    }

    @Override
    public List<Emprendimiento> listar() {
        return emprendimientoRepository.findAll();
    }

    @Override
    public Emprendimiento buscarPorId(Integer id) {
        return emprendimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));
    }

    @Override
    public Emprendimiento actualizar(Integer id, Emprendimiento actualizado) {
        Emprendimiento existente = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));
        existente.setNombre(actualizado.getNombre());
        existente.setDescripcion(actualizado.getDescripcion());
        existente.setLogoUrl(actualizado.getLogoUrl());
        if (actualizado.getCategoria()  != null) existente.setCategoria(actualizado.getCategoria());
        if (actualizado.getEstado()     != null) existente.setEstado(actualizado.getEstado());
        if (actualizado.getDestacado()  != null) existente.setDestacado(actualizado.getDestacado());

        Emprendimiento saved = emprendimientoRepository.save(existente);

        try { mongoSync.sincronizarEmprendimiento(saved); }
        catch (Exception e) { /* no bloquea */ }

        return saved;
    }

    @Override
    public void eliminar(Integer id) {
        emprendimientoRepository.deleteById(id);
    }

    @Override
    public List<Emprendimiento> listarPorUsuario(Integer idUsuario) {
        return emprendimientoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Emprendimiento> listarDestacados() {
        return emprendimientoRepository.findByDestacadoTrue();
    }

    @Override
    public void toggleDestacado(Integer id) {
        Emprendimiento e = emprendimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));
        e.setDestacado(e.getDestacado() == null || !e.getDestacado());
        Emprendimiento saved = emprendimientoRepository.save(e);
        try { mongoSync.sincronizarEmprendimiento(saved); }
        catch (Exception ex) { /* no bloquea */ }
    }
}
