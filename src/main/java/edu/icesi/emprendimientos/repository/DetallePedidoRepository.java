package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
}