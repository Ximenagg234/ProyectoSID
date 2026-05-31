package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stockDisponible;
    private String primeraImagenUrl;
    private Integer idEmprendimiento;
    private String nombreEmprendimiento;
    private String nombreEstado;
}
