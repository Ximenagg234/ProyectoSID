package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.rest.dto.ProductoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.ProductoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(source = "emprendimiento.nombre", target = "nombreEmprendimiento")
    @Mapping(source = "estado.nombre", target = "nombreEstado")
    ProductoResponseDTO toDto(Producto producto);

    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "emprendimiento", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "imagenes", ignore = true)
    Producto toEntity(ProductoRequestDTO dto);
}
