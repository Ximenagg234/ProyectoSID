package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.repository.PedidoRepository;
import edu.icesi.emprendimientos.service.PedidoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstadoRepository estadoRepository;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                             EstadoRepository estadoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public Pedido crearPedido(Pedido pedido) {
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new RuntimeException("El pedido debe tener productos");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (DetallePedido d : pedido.getDetalles()) {
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
    public List<Pedido> listarPorEmprendedor(Integer idUsuario) {
        return pedidoRepository.findByEmprendimiento_Usuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Pedido> listarRecibidosPorEmprendedor(Integer idUsuario) {
        return pedidoRepository.findByEmprendimiento_Usuario_IdUsuario(idUsuario);
    }

    @Override
    public void actualizarEstado(Integer idPedido, Integer idEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        Estado estado = estadoRepository.findById(idEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);
    }

    @Override
    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }
}
