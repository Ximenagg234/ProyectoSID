package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "correo_institucional", nullable = false, unique = true)
    private String correoInstitucional;

    @Column(name = "programa_academico", nullable = false)
    private String programaAcademico;

    @Column(name = "semestre_academico", nullable = false)
    private Integer semestreAcademico;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "clave", nullable = false)
    private String clave;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UsuarioRol> roles;

    public List<UsuarioRol> getRoles() {
        return roles;
    }

    public String getClave() {
        return clave;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }
}