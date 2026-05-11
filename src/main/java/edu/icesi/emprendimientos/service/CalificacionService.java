package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Calificacion;
import edu.icesi.emprendimientos.repository.CalificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;

    public CalificacionService(CalificacionRepository calificacionRepository) {
        this.calificacionRepository = calificacionRepository;
    }

    public List<Calificacion> listarPorEmprendimiento(Integer idEmprendimiento) {
        return calificacionRepository.findAll().stream()
                .filter(c -> c.getEmprendimiento() != null &&
                        c.getEmprendimiento().getIdEmprendimiento().equals(idEmprendimiento))
                .toList();
    }

    public Double promedioPorEmprendimiento(Integer idEmprendimiento) {
        List<Calificacion> calificaciones = listarPorEmprendimiento(idEmprendimiento);
        if (calificaciones.isEmpty()) return 0.0;
        return calificaciones.stream()
                .mapToInt(Calificacion::getPuntuacion)
                .average()
                .orElse(0.0);
    }

    public boolean yaCalifico(Integer idPedido) {
        return calificacionRepository.findAll().stream()
                .anyMatch(c -> c.getPedido() != null &&
                        c.getPedido().getIdPedido().equals(idPedido));
    }

    public Calificacion guardar(Calificacion calificacion) {
        return calificacionRepository.save(calificacion);
    }
}
