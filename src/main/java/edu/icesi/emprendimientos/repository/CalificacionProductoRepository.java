package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.CalificacionProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalificacionProductoRepository extends JpaRepository<CalificacionProducto, Integer> {

    List<CalificacionProducto> findByProducto_IdProducto(Integer idProducto);

    boolean existsByUsuario_IdUsuarioAndProducto_IdProducto(Integer idUsuario, Integer idProducto);
}
