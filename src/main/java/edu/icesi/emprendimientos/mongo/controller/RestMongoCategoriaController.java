package edu.icesi.emprendimientos.mongo.controller;

import edu.icesi.emprendimientos.mongo.repository.EmprendimientoMongoRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Profile("sid")
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias MongoDB")
@SecurityRequirement(name = "BearerAuth")
public class RestMongoCategoriaController {

    // Categorías estáticas — equivalente al data.sql
    private static final List<Map<String, Object>> CATEGORIAS_FIJAS = List.of(
        Map.of("idCategoria", 1, "nombre", "Tecnologia",  "descripcion", "Productos tecnologicos"),
        Map.of("idCategoria", 2, "nombre", "Moda",        "descripcion", "Ropa y accesorios"),
        Map.of("idCategoria", 3, "nombre", "Comida",      "descripcion", "Alimentos y snacks"),
        Map.of("idCategoria", 4, "nombre", "Bebidas",     "descripcion", "Bebidas y cafes"),
        Map.of("idCategoria", 5, "nombre", "Arte",        "descripcion", "Productos artisticos"),
        Map.of("idCategoria", 6, "nombre", "Servicios",   "descripcion", "Servicios y asesorias")
    );

    private final EmprendimientoMongoRepository empRepo;

    public RestMongoCategoriaController(EmprendimientoMongoRepository empRepo) {
        this.empRepo = empRepo;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll() {
        return ResponseEntity.ok(CATEGORIAS_FIJAS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        return CATEGORIAS_FIJAS.stream()
            .filter(c -> id.equals(c.get("idCategoria")))
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
