package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionResponseDTO {
    private Integer idCalificacion;
    private Integer puntuacion;
    private String comentario;
    private Date fecha;
    private Integer idUsuario;
    private String nombreUsuario;
    private Integer idEmprendimiento;
    private Integer idPedido;
}
