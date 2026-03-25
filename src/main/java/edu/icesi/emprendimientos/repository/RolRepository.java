package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}