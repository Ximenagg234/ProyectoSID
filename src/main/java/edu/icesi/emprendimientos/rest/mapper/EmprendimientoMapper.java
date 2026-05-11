package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.rest.dto.EmprendimientoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.EmprendimientoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmprendimientoMapper {

    @Mapping(source = "usuario.nombreCompleto", target = "nombreUsuario")
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    @Mapping(source = "semestre.periodo", target = "nombreSemestre")
    @Mapping(source = "estado.nombre", target = "nombreEstado")
    EmprendimientoResponseDTO toDto(Emprendimiento emprendimiento);

    @Mapping(target = "idEmprendimiento", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "semestre", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "productos", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    Emprendimiento toEntity(EmprendimientoRequestDTO dto);
}
