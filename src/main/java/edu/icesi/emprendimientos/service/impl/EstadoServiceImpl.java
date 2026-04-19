package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.service.EstadoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoServiceImpl implements EstadoService {

    private final EstadoRepository estadoRepository;

    public EstadoServiceImpl(EstadoRepository estadoRepository) {
        this.estadoRepository = estadoRepository;
    }

    @Override
    public List<Estado> listar() {
        return estadoRepository.findAll();
    }

    @Override
    public Estado buscarPorId(Integer id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
    }
}