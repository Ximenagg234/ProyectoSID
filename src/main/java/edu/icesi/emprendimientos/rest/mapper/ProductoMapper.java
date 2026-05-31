package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.ImagenProducto;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.rest.dto.ProductoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.ProductoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(source = "emprendimiento.idEmprendimiento", target = "idEmprendimiento")
    @Mapping(source = "emprendimiento.nombre", target = "nombreEmprendimiento")
    @Mapping(source = "estado.nombre", target = "nombreEstado")
    @Mapping(source = "imagenes", target = "primeraImagenUrl", qualifiedByName = "firstImageUrl")
    ProductoResponseDTO toDto(Producto producto);

    @Named("firstImageUrl")
    default String firstImageUrl(List<ImagenProducto> imagenes) {
        if (imagenes == null || imagenes.isEmpty()) return null;
        return imagenes.get(0).getUrlImagen();
    }

    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "emprendimiento", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    Producto toEntity(ProductoRequestDTO dto);
}
