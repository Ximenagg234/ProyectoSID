package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
}

