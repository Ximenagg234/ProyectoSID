package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmprendimientoRequestDTO {
    private String nombre;
    private String descripcion;
    private String logoUrl;
    private Integer idUsuario;
    private Integer idCategoria;
    private Integer idSemestre;
    private Integer idEstado;
}
