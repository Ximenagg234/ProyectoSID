package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.Categoria;
import edu.icesi.emprendimientos.rest.dto.CategoriaRequestDTO;
import edu.icesi.emprendimientos.rest.dto.CategoriaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponseDTO toDto(Categoria categoria);

    @Mapping(target = "idCategoria", ignore = true)
    @Mapping(target = "emprendimientos", ignore = true)
    Categoria toEntity(CategoriaRequestDTO dto);
}
