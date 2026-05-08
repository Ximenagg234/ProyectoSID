package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    Optional<Estado> findByNombre(String nombre);
}
