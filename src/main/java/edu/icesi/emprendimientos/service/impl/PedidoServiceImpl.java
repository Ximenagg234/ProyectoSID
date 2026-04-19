package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.repository.PedidoRepository;
import edu.icesi.emprendimientos.service.PedidoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido crearPedido(Pedido pedido) {

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new RuntimeException("El pedido debe tener productos");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (DetallePedido d : pedido.getDetalles()) {

            // aseguramos una relación bidireccional
            d.setPedido(pedido);

            total = total.add(d.getSubtotal());
        }

        pedido.setTotal(total);

        return pedidoRepository.save(pedido);
    }

    @Override
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido buscarPorId(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    @Override
    public List<Pedido> listarPorUsuario(Integer idUsuario) {
        return pedidoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }
}