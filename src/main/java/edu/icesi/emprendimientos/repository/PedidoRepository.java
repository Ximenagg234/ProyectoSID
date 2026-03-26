package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}