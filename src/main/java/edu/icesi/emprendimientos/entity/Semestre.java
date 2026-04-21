package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "semestre")
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_semestre")
    private Integer idSemestre;

    @Column(name = "periodo", nullable = false, unique = true)
    private String periodo;

    @Column(name = "fecha_inicio", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @ManyToOne
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @OneToMany(mappedBy = "semestre")
    private List<Emprendimiento> emprendimientos;

    public Semestre() {}

    public Semestre(Integer idSemestre, String periodo, Date fechaInicio, Date fechaFin, Estado estado, List<Emprendimiento> emprendimientos) {
        this.idSemestre = idSemestre;
        this.periodo = periodo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.emprendimientos = emprendimientos;
    }

    public Integer getIdSemestre() { return idSemestre; }
    public void setIdSemestre(Integer idSemestre) { this.idSemestre = idSemestre; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Date getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio = fechaInicio; }

    public Date getFechaFin() { return fechaFin; }
    public void setFechaFin(Date fechaFin) { this.fechaFin = fechaFin; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public List<Emprendimiento> getEmprendimientos() { return emprendimientos; }
    public void setEmprendimientos(List<Emprendimiento> emprendimientos) { this.emprendimientos = emprendimientos; }
}
