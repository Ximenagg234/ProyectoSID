package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer idReporte;

    @Column(name = "fecha_generacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaGeneracion;

    @Column(name = "periodo", nullable = false)
    private String periodo;

    @Column(name = "ruta_archivo")
    private String rutaArchivo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public Reporte() {}

    public Reporte(Integer idReporte, Date fechaGeneracion, String periodo, String rutaArchivo, Usuario usuario) {
        this.idReporte = idReporte;
        this.fechaGeneracion = fechaGeneracion;
        this.periodo = periodo;
        this.rutaArchivo = rutaArchivo;
        this.usuario = usuario;
    }

    public Integer getIdReporte() { return idReporte; }
    public void setIdReporte(Integer idReporte) { this.idReporte = idReporte; }

    public Date getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(Date fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
