package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuario")
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

    public Usuario() {}

    public Usuario(Integer idUsuario, String nombreCompleto, String correoInstitucional,
                   String programaAcademico, Integer semestreAcademico, String fotoPerfil,
                   String clave, List<UsuarioRol> roles) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correoInstitucional = correoInstitucional;
        this.programaAcademico = programaAcademico;
        this.semestreAcademico = semestreAcademico;
        this.fotoPerfil = fotoPerfil;
        this.clave = clave;
        this.roles = roles;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }

    public String getProgramaAcademico() { return programaAcademico; }
    public void setProgramaAcademico(String programaAcademico) { this.programaAcademico = programaAcademico; }

    public Integer getSemestreAcademico() { return semestreAcademico; }
    public void setSemestreAcademico(Integer semestreAcademico) { this.semestreAcademico = semestreAcademico; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public List<UsuarioRol> getRoles() { return roles; }
    public void setRoles(List<UsuarioRol> roles) { this.roles = roles; }
}
