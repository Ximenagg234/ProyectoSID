package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
}