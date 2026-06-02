package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.ImagenProducto;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.service.FileStorageService;
import edu.icesi.emprendimientos.service.ImagenProductoService;
import edu.icesi.emprendimientos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;

@Profile("!sid")
@RestController
@RequestMapping("/api/productos/{idProducto}/imagenes")
@Tag(name = "Imágenes de productos", description = "Gestión de imágenes de productos")
@SecurityRequirement(name = "BearerAuth")
public class RestImagenProductoController {

    private final ImagenProductoService imagenService;
    private final ProductoService productoService;
    private final FileStorageService fileStorageService;

    public RestImagenProductoController(ImagenProductoService imagenService,
                                        ProductoService productoService,
                                        FileStorageService fileStorageService) {
        this.imagenService = imagenService;
        this.productoService = productoService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    @Operation(summary = "Listar imágenes de un producto")
    public ResponseEntity<List<Map<String, Object>>> getImagenes(@PathVariable Integer idProducto) {
        List<Map<String, Object>> result = imagenService.listarPorProducto(idProducto).stream()
                .map(img -> Map.<String, Object>of(
                        "idImagen", img.getIdImagen(),
                        "urlImagen", img.getUrlImagen()
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Subir nueva imagen para un producto")
    public ResponseEntity<Map<String, Object>> uploadImagen(
            @PathVariable Integer idProducto,
            @RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.guardar(file, "productos");
            if (url == null) return ResponseEntity.badRequest().build();

            Producto producto = productoService.buscarPorId(idProducto);
            ImagenProducto imagen = new ImagenProducto();
            imagen.setUrlImagen(url);
            imagen.setProducto(producto);
            ImagenProducto saved = imagenService.guardar(imagen);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    Map.of("idImagen", saved.getIdImagen(), "urlImagen", saved.getUrlImagen())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{idImagen}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Eliminar imagen de un producto")
    public ResponseEntity<Void> deleteImagen(
            @PathVariable Integer idProducto,
            @PathVariable Integer idImagen) {
        imagenService.eliminar(idImagen);
        return ResponseEntity.noContent().build();
    }
}
