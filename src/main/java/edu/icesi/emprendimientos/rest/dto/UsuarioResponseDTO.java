package edu.icesi.emprendimientos.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nombreCompleto;
    private String correoInstitucional;
    private String programaAcademico;
    private Integer semestreAcademico;
    private String fotoPerfil;
    private List<String> roles;
}
