package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {

    Optional<Semestre> findFirstByEstado_NombreOrderByFechaInicioDesc(String estadoNombre);
}