package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {
    private Integer idCategoria;
    private String nombre;
    private String descripcion;
}
