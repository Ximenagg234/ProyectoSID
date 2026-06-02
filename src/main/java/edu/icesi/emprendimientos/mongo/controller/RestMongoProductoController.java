package edu.icesi.emprendimientos.mongo.controller;

import edu.icesi.emprendimientos.mongo.document.EmprendimientoDocument;
import edu.icesi.emprendimientos.mongo.repository.EmprendimientoMongoRepository;
import edu.icesi.emprendimientos.rest.dto.ProductoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.ProductoResponseDTO;
import edu.icesi.emprendimientos.service.FileStorageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Profile("sid")
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos MongoDB")
@SecurityRequirement(name = "BearerAuth")
public class RestMongoProductoController {

    private final EmprendimientoMongoRepository empRepo;
    private final FileStorageService            fileStorage;

    public RestMongoProductoController(EmprendimientoMongoRepository empRepo,
                                        FileStorageService fileStorage) {
        this.empRepo     = empRepo;
        this.fileStorage = fileStorage;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> getAll() {
        List<ProductoResponseDTO> list = new ArrayList<>();
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                emp.getProductos().forEach(p -> list.add(toDto(p, emp)));
            }
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponseDTO> getById(@PathVariable Integer idProducto) {
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                Optional<EmprendimientoDocument.ProductoEmbed> found = emp.getProductos().stream()
                    .filter(p -> idProducto.equals(p.getIdProductoSql())).findFirst();
                if (found.isPresent()) return ResponseEntity.ok(toDto(found.get(), emp));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> create(@RequestBody ProductoRequestDTO dto) {
        return empRepo.findByIdEmprendimientoSql(dto.getIdEmprendimiento()).map(emp -> {
            EmprendimientoDocument.ProductoEmbed p = new EmprendimientoDocument.ProductoEmbed();
            int newId = emp.getProductos() == null ? 1 : emp.getProductos().stream()
                .mapToInt(x -> x.getIdProductoSql() != null ? x.getIdProductoSql() : 0)
                .max().orElse(0) + 1;
            // Ensure unique across all emprendimientos
            int globalMax = empRepo.findAll().stream().flatMap(e -> e.getProductos() == null ? java.util.stream.Stream.empty() : e.getProductos().stream())
                .mapToInt(x -> x.getIdProductoSql() != null ? x.getIdProductoSql() : 0).max().orElse(0);
            p.setIdProductoSql(Math.max(newId, globalMax + 1));
            applyDto(p, dto);
            if (emp.getProductos() == null) emp.setProductos(new ArrayList<>());
            emp.getProductos().add(p);
            empRepo.save(emp);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDto(p, emp));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoResponseDTO> update(@PathVariable Integer idProducto,
                                                       @RequestBody ProductoRequestDTO dto) {
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                for (EmprendimientoDocument.ProductoEmbed p : emp.getProductos()) {
                    if (idProducto.equals(p.getIdProductoSql())) {
                        applyDto(p, dto);
                        p.setUpdatedAt(new Date());
                        empRepo.save(emp);
                        return ResponseEntity.ok(toDto(p, emp));
                    }
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> delete(@PathVariable Integer idProducto) {
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                boolean removed = emp.getProductos().removeIf(p -> idProducto.equals(p.getIdProductoSql()));
                if (removed) { empRepo.save(emp); return ResponseEntity.noContent().build(); }
            }
        }
        return ResponseEntity.notFound().build();
    }

    // ── Imágenes ─────────────────────────────────────────────────

    @GetMapping("/{idProducto}/imagenes")
    public ResponseEntity<List<Map<String, Object>>> getImagenes(@PathVariable Integer idProducto) {
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                Optional<EmprendimientoDocument.ProductoEmbed> found = emp.getProductos().stream()
                    .filter(p -> idProducto.equals(p.getIdProductoSql())).findFirst();
                if (found.isPresent()) {
                    List<String> imgs = found.get().getImagenes();
                    if (imgs == null) imgs = new ArrayList<>();
                    List<Map<String, Object>> result = new ArrayList<>();
                    for (int i = 0; i < imgs.size(); i++) {
                        result.add(Map.of("idImagen", i + 1, "urlImagen", imgs.get(i)));
                    }
                    return ResponseEntity.ok(result);
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/{idProducto}/imagenes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImagen(@PathVariable Integer idProducto,
                                           @RequestParam("file") MultipartFile file) {
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                for (EmprendimientoDocument.ProductoEmbed p : emp.getProductos()) {
                    if (idProducto.equals(p.getIdProductoSql())) {
                        String url;
                        try { url = fileStorage.guardar(file, "productos"); }
                        catch (Exception ex) { return ResponseEntity.status(500).build(); }
                        if (url == null) return ResponseEntity.badRequest().build();
                        if (p.getImagenes() == null) p.setImagenes(new ArrayList<>());
                        p.getImagenes().add(url);
                        empRepo.save(emp);
                        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of("urlImagen", url));
                    }
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{idProducto}/imagenes/{idImagen}")
    public ResponseEntity<Void> deleteImagen(@PathVariable Integer idProducto,
                                              @PathVariable Integer idImagen) {
        for (EmprendimientoDocument emp : empRepo.findAll()) {
            if (emp.getProductos() != null) {
                for (EmprendimientoDocument.ProductoEmbed p : emp.getProductos()) {
                    if (idProducto.equals(p.getIdProductoSql())) {
                        if (p.getImagenes() != null && idImagen <= p.getImagenes().size()) {
                            p.getImagenes().remove(idImagen - 1);
                            empRepo.save(emp);
                        }
                        return ResponseEntity.noContent().build();
                    }
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void applyDto(EmprendimientoDocument.ProductoEmbed p, ProductoRequestDTO dto) {
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStockDisponible(dto.getStockDisponible());
        p.setEstado(dto.getIdEstado() != null && dto.getIdEstado() == 2 ? "INACTIVO" : "ACTIVO");
        if (p.getImagenes() == null) p.setImagenes(new ArrayList<>());
        Date now = new Date();
        if (p.getCreatedAt() == null) p.setCreatedAt(now);
        p.setUpdatedAt(now);
    }

    ProductoResponseDTO toDto(EmprendimientoDocument.ProductoEmbed p, EmprendimientoDocument emp) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setIdProducto(p.getIdProductoSql());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setStockDisponible(p.getStockDisponible());
        dto.setNombreEstado(p.getEstado() != null ? p.getEstado() : "ACTIVO");
        dto.setIdEmprendimiento(emp.getIdEmprendimientoSql());
        dto.setNombreEmprendimiento(emp.getNombre());
        String primera = (p.getImagenes() != null && !p.getImagenes().isEmpty())
            ? p.getImagenes().get(0) : null;
        dto.setPrimeraImagenUrl(primera);
        return dto;
    }
}
