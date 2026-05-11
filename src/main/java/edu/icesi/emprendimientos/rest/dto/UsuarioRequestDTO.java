package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    private String nombreCompleto;
    private String correoInstitucional;
    private String programaAcademico;
    private Integer semestreAcademico;
    private String fotoPerfil;
    private String clave;
}
