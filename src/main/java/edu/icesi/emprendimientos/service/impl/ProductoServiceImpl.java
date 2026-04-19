package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.repository.ProductoRepository;
import edu.icesi.emprendimientos.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public Producto guardar(Producto producto) {

        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new RuntimeException("El producto debe tener nombre");
        }

        if (producto.getPrecio() == null || producto.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        if (producto.getStockDisponible() == null || producto.getStockDisponible() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        if (producto.getEmprendimiento() == null) {
            throw new RuntimeException("El producto debe pertenecer a un emprendimiento");
        }

        if (producto.getEstado() == null) {
            throw new RuntimeException("El producto debe tener un estado");
        }

        return productoRepository.save(producto);
    }

    @Override
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Override
    public Producto buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    public Producto actualizar(Integer id, Producto actualizado) {

        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        existente.setNombre(actualizado.getNombre());
        existente.setDescripcion(actualizado.getDescripcion());
        existente.setPrecio(actualizado.getPrecio());
        existente.setStockDisponible(actualizado.getStockDisponible());

        return productoRepository.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> listarPorEmprendimiento(Integer idEmprendimiento) {
        return productoRepository.findByEmprendimiento_IdEmprendimiento(idEmprendimiento);
    }
}
