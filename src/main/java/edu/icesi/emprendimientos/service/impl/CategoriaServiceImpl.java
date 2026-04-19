package edu.icesi.emprendimientos.service.impl;

import edu.icesi.emprendimientos.entity.Categoria;
import edu.icesi.emprendimientos.repository.CategoriaRepository;
import edu.icesi.emprendimientos.service.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public Categoria guardar(Categoria categoria) {

        if (categoria.getNombre() == null || categoria.getNombre().isEmpty()) {
            throw new RuntimeException("La categoría debe tener nombre");
        }

        return categoriaRepository.save(categoria);
    }

    @Override
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @Override
    public Categoria buscarPorId(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    @Override
    public Categoria actualizar(Integer id, Categoria actualizada) {

        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        existente.setNombre(actualizada.getNombre());
        existente.setDescripcion(actualizada.getDescripcion());

        return categoriaRepository.save(existente);
    }

    @Override
    public void eliminar(Integer id) {
        categoriaRepository.deleteById(id);
    }
}