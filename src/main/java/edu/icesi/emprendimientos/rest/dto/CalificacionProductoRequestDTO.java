package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionProductoRequestDTO {
    private Integer puntuacion;   // 1-5
    private String comentario;
    private Integer idUsuario;
}
