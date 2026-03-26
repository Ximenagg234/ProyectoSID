package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprendimientoRepository extends JpaRepository<Emprendimiento, Integer> {
}
