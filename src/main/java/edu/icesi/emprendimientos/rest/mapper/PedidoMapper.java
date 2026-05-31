package edu.icesi.emprendimientos.rest.mapper;

import edu.icesi.emprendimientos.entity.DetallePedido;
import edu.icesi.emprendimientos.entity.Pedido;
import edu.icesi.emprendimientos.rest.dto.DetallePedidoResponseDTO;
import edu.icesi.emprendimientos.rest.dto.PedidoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(source = "emprendimiento.idEmprendimiento", target = "idEmprendimiento")
    @Mapping(source = "emprendimiento.nombre", target = "nombreEmprendimiento")
    @Mapping(source = "usuario.nombreCompleto", target = "nombreUsuario")
    @Mapping(source = "estado.nombre", target = "nombreEstado")
    PedidoResponseDTO toDto(Pedido pedido);

    @Mapping(source = "producto.nombre", target = "nombreProducto")
    DetallePedidoResponseDTO detalleToDto(DetallePedido detalle);

    @Mapping(target = "idPedido", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "emprendimiento", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    @Mapping(target = "fechaPedido", ignore = true)
    @Mapping(target = "total", ignore = true)
    Pedido toEntity(PedidoRequestDTO dto);
}
