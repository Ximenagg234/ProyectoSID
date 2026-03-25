package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.UsuarioRol;
import edu.icesi.emprendimientos.entity.keys.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {
}