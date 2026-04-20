package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombreCompleto(String nombreCompleto);
}