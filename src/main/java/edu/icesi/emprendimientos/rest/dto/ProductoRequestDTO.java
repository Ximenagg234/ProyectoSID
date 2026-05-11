package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stockDisponible;
    private Integer idEmprendimiento;
    private Integer idEstado;
}
