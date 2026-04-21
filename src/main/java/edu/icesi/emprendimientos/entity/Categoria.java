package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "categoria")
    private List<Emprendimiento> emprendimientos;

    public Categoria() {}

    public Categoria(Integer idCategoria, String nombre, String descripcion, List<Emprendimiento> emprendimientos) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.emprendimientos = emprendimientos;
    }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<Emprendimiento> getEmprendimientos() { return emprendimientos; }
    public void setEmprendimientos(List<Emprendimiento> emprendimientos) { this.emprendimientos = emprendimientos; }
}
