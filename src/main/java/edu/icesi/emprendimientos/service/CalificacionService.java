package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Calificacion;
import edu.icesi.emprendimientos.mongo.service.MongoSyncService;
import edu.icesi.emprendimientos.repository.CalificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final MongoSyncService       mongoSync;

    public CalificacionService(CalificacionRepository calificacionRepository,
                               MongoSyncService mongoSync) {
        this.calificacionRepository = calificacionRepository;
        this.mongoSync              = mongoSync;
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

    public Optional<Calificacion> buscarPorId(Integer idCalificacion) {
        return calificacionRepository.findById(idCalificacion);
    }

    public Calificacion guardar(Calificacion calificacion) {
        Calificacion saved = calificacionRepository.save(calificacion);

        // Dual-write a MongoDB + actualizar métricas (trigger)
        try { mongoSync.sincronizarCalificacion(saved); }
        catch (Exception e) { /* no bloquea */ }

        return saved;
    }

    public Calificacion actualizar(Integer idCalificacion, Integer idUsuario,
                                   Integer puntuacion, String comentario) {
        Calificacion cal = calificacionRepository.findById(idCalificacion)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        if (!cal.getUsuario().getIdUsuario().equals(idUsuario))
            throw new SecurityException("No tienes permiso para editar esta calificación");
        cal.setPuntuacion(puntuacion);
        cal.setComentario(comentario);
        Calificacion saved = calificacionRepository.save(cal);

        try { mongoSync.sincronizarCalificacion(saved); }
        catch (Exception e) { /* no bloquea */ }

        return saved;
    }

    public void eliminar(Integer idCalificacion, Integer idUsuario) {
        Calificacion cal = calificacionRepository.findById(idCalificacion)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        if (!cal.getUsuario().getIdUsuario().equals(idUsuario))
            throw new SecurityException("No tienes permiso para eliminar esta calificación");
        calificacionRepository.delete(cal);
    }
}
