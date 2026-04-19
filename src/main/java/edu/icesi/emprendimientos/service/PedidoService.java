package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.Pedido;

import java.util.List;

public interface PedidoService {

    Pedido crearPedido(Pedido pedido);

    List<Pedido> listar();

    Pedido buscarPorId(Integer id);

    List<Pedido> listarPorUsuario(Integer idUsuario);

    void eliminar(Integer id);
}