package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}