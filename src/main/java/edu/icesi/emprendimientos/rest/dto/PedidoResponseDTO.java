package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Integer idPedido;
    private String nombreEmprendimiento;
    private String nombreUsuario;
    private String nombreEstado;
    private List<DetallePedidoResponseDTO> detalles;
    private BigDecimal total;
}
