package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.repository.DetallePedidoRepository;
import edu.icesi.emprendimientos.service.DetallePedidoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;

    public DetallePedidoServiceImpl(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    @Override
    public DetallePedido crearDetalle(DetallePedido detalle) {

        if (detalle.getProducto() == null) {
            throw new RuntimeException("Debe seleccionar un producto");
        }

        if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
            throw new RuntimeException("Cantidad inválida");
        }

        Producto producto = detalle.getProducto();

        // VALIDAR STOCK
        if (producto.getStockDisponible() < detalle.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        // DESCONTAR STOCK
        producto.setStockDisponible(
                producto.getStockDisponible() - detalle.getCantidad()
        );

        BigDecimal precio = producto.getPrecio();

        detalle.setPrecioUnitario(precio);
        detalle.setSubtotal(
                precio.multiply(BigDecimal.valueOf(detalle.getCantidad()))
        );

        return detallePedidoRepository.save(detalle);
    }
}