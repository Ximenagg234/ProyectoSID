package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByUsuario_IdUsuario(Integer idUsuario);
}