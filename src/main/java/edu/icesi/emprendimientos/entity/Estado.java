package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "estado")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "estado")
    private List<Emprendimiento> emprendimientos;

    @OneToMany(mappedBy = "estado")
    private List<Semestre> semestres;

    public Estado() {}

    public Estado(Integer idEstado, String nombre, List<Emprendimiento> emprendimientos, List<Semestre> semestres) {
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.emprendimientos = emprendimientos;
        this.semestres = semestres;
    }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Emprendimiento> getEmprendimientos() { return emprendimientos; }
    public void setEmprendimientos(List<Emprendimiento> emprendimientos) { this.emprendimientos = emprendimientos; }

    public List<Semestre> getSemestres() { return semestres; }
    public void setSemestres(List<Semestre> semestres) { this.semestres = semestres; }
}
