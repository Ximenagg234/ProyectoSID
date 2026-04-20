package edu.icesi.emprendimientos.repository;

import edu.icesi.emprendimientos.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEmprendimiento_IdEmprendimiento(Integer idEmprendimiento);

    List<Producto> findByEmprendimiento_Categoria_IdCategoria(Integer idCategoria);

    List<Producto> findByEstado_Nombre(String estadoNombre);

    List<Producto> findByEmprendimiento_Categoria_IdCategoriaAndEstado_Nombre(
            Integer idCategoria, String estadoNombre);
}
