package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprendimientoResponseDTO {
    private Integer idEmprendimiento;
    private String nombre;
    private String descripcion;
    private String logoUrl;
    private Boolean destacado;
    private String nombreUsuario;
    private String nombreCategoria;
    private String nombreSemestre;
    private String nombreEstado;
}
