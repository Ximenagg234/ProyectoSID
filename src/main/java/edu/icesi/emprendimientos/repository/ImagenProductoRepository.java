package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.ImagenProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Integer> {
    List<ImagenProducto> findByProducto_IdProducto(Integer idProducto);
}