package edu.icesi.emprendimientos.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Entity
@Table(name = "reporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}