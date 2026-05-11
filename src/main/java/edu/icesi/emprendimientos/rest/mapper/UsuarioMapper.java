package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.Usuario;
import edu.icesi.emprendimientos.rest.dto.UsuarioRequestDTO;
import edu.icesi.emprendimientos.rest.dto.UsuarioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "roles", expression = "java(usuario.getRoles() == null ? java.util.Collections.emptyList() : usuario.getRoles().stream().map(ur -> ur.getRol().getNombre()).collect(java.util.stream.Collectors.toList()))")
    UsuarioResponseDTO toDto(Usuario usuario);

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Usuario toEntity(UsuarioRequestDTO dto);
}
