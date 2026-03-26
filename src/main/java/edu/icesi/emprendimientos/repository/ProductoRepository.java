package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}