package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.Rol;
import edu.icesi.emprendimientos.rest.dto.RolRequestDTO;
import edu.icesi.emprendimientos.rest.dto.RolResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolMapper {

    RolResponseDTO toDto(Rol rol);

    @Mapping(target = "idRol", ignore = true)
    @Mapping(target = "permisos", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Rol toEntity(RolRequestDTO dto);
}
