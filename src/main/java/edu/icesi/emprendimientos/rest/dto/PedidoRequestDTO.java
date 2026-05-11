package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {
    private Integer idEmprendimiento;
    private Integer idUsuario;
    private Integer idEstado;
    private List<DetallePedidoRequestDTO> detalles;
}
