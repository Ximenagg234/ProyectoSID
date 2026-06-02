package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.CalificacionProducto;
import edu.icesi.emprendimientos.repository.CalificacionProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionProductoService {

    private final CalificacionProductoRepository repo;

    public CalificacionProductoService(CalificacionProductoRepository repo) {
        this.repo = repo;
    }

    public List<CalificacionProducto> listarPorProducto(Integer idProducto) {
        return repo.findByProducto_IdProducto(idProducto);
    }

    public Double promedioPorProducto(Integer idProducto) {
        List<CalificacionProducto> lista = listarPorProducto(idProducto);
        if (lista.isEmpty()) return 0.0;
        return lista.stream().mapToInt(CalificacionProducto::getPuntuacion).average().orElse(0.0);
    }

    public boolean yaCalificoProducto(Integer idUsuario, Integer idProducto) {
        return repo.existsByUsuario_IdUsuarioAndProducto_IdProducto(idUsuario, idProducto);
    }

    public CalificacionProducto guardar(CalificacionProducto cal) {
        return repo.save(cal);
    }

    public CalificacionProducto actualizar(Integer idCalificacion, Integer idUsuario,
                                           Integer puntuacion, String comentario) {
        CalificacionProducto cal = repo.findById(idCalificacion)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        if (!cal.getUsuario().getIdUsuario().equals(idUsuario))
            throw new SecurityException("No tienes permiso para editar esta calificación");
        cal.setPuntuacion(puntuacion);
        cal.setComentario(comentario);
        return repo.save(cal);
    }

    public void eliminar(Integer idCalificacion, Integer idUsuario) {
        CalificacionProducto cal = repo.findById(idCalificacion)
                .orElseThrow(() -> new RuntimeException("Calificación no encontrada"));
        if (!cal.getUsuario().getIdUsuario().equals(idUsuario))
            throw new SecurityException("No tienes permiso para eliminar esta calificación");
        repo.delete(cal);
    }
}
