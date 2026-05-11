package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprendimientoRepository extends JpaRepository<Emprendimiento, Integer> {

    List<Emprendimiento> findByEstado_Nombre(String estadoNombre);

    List<Emprendimiento> findByUsuario_IdUsuario(Integer idUsuario);

    List<Emprendimiento> findByDestacadoTrue();
}
