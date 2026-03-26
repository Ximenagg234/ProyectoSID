package edu.icesi.emprendimientos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagen_producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}