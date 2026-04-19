package edu.icesi.emprendimientos.service;

import edu.icesi.emprendimientos.entity.ImagenProducto;

import java.util.List;

public interface ImagenProductoService {

    ImagenProducto guardar(ImagenProducto imagen);

    List<ImagenProducto> listarPorProducto(Integer idProducto);

    void eliminar(Integer id);
}