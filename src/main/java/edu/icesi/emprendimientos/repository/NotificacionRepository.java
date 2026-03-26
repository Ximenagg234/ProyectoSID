package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
}

