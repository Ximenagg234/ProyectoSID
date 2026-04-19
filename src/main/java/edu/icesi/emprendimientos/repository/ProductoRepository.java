package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEmprendimiento_IdEmprendimiento(Integer idEmprendimiento);
}