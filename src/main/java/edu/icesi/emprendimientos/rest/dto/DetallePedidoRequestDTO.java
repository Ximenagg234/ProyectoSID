package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequestDTO {
    private Integer idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
