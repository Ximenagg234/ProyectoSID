package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "imagen_producto")
public class ImagenProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Integer idImagen;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    public ImagenProducto() {}

    public ImagenProducto(Integer idImagen, String urlImagen, Producto producto) {
        this.idImagen = idImagen;
        this.urlImagen = urlImagen;
        this.producto = producto;
    }

    public Integer getIdImagen() { return idImagen; }
    public void setIdImagen(Integer idImagen) { this.idImagen = idImagen; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}
