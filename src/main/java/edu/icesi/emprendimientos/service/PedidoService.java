package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Pedido;

import java.util.List;

public interface PedidoService {
    Pedido crearPedido(Pedido pedido);
    List<Pedido> listar();
    Pedido buscarPorId(Integer id);
    List<Pedido> listarPorUsuario(Integer idUsuario);
    List<Pedido> listarPorEmprendedor(Integer idUsuario);
    void actualizarEstado(Integer idPedido, Integer idEstado);
    void eliminar(Integer id);
}
