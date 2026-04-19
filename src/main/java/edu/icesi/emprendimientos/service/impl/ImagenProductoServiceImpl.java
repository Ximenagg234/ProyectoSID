package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.ImagenProducto;
import edu.icesi.emprendimientos.repository.ImagenProductoRepository;
import edu.icesi.emprendimientos.service.ImagenProductoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImagenProductoServiceImpl implements ImagenProductoService {

    private final ImagenProductoRepository imagenProductoRepository;

    public ImagenProductoServiceImpl(ImagenProductoRepository imagenProductoRepository) {
        this.imagenProductoRepository = imagenProductoRepository;
    }

    @Override
    public ImagenProducto guardar(ImagenProducto imagen) {

        if (imagen.getUrl() == null || imagen.getUrl().isEmpty()) {
            throw new RuntimeException("La imagen debe tener una URL");
        }

        if (imagen.getProducto() == null) {
            throw new RuntimeException("La imagen debe pertenecer a un producto");
        }

        return imagenProductoRepository.save(imagen);
    }

    @Override
    public List<ImagenProducto> listarPorProducto(Integer idProducto) {
        return imagenProductoRepository.findByProducto_IdProducto(idProducto);
    }

    @Override
    public void eliminar(Integer id) {
        imagenProductoRepository.deleteById(id);
    }
}