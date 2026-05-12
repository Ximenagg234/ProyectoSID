package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.Emprendimiento;
import edu.icesi.emprendimientos.entity.Estado;
import edu.icesi.emprendimientos.entity.Producto;
import edu.icesi.emprendimientos.repository.EmprendimientoRepository;
import edu.icesi.emprendimientos.repository.EstadoRepository;
import edu.icesi.emprendimientos.rest.dto.ProductoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.ProductoResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.ProductoMapper;
import edu.icesi.emprendimientos.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD de productos")
@SecurityRequirement(name = "BearerAuth")
public class RestProductoController {

    private final ProductoService productoService;
    private final ProductoMapper productoMapper;
    private final EmprendimientoRepository emprendimientoRepository;
    private final EstadoRepository estadoRepository;

    public RestProductoController(ProductoService productoService,
                                   ProductoMapper productoMapper,
                                   EmprendimientoRepository emprendimientoRepository,
                                   EstadoRepository estadoRepository) {
        this.productoService = productoService;
        this.productoMapper = productoMapper;
        this.emprendimientoRepository = emprendimientoRepository;
        this.estadoRepository = estadoRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "401", description = "No autorizado")
    public ResponseEntity<List<ProductoResponseDTO>> getAll() {
        return ResponseEntity.ok(productoService.listar().stream()
                .map(productoMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<ProductoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(productoMapper.toDto(productoService.buscarPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Crear producto")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<ProductoResponseDTO> create(@RequestBody ProductoRequestDTO dto) {
        Producto p = productoMapper.toEntity(dto);
        resolveRelations(p, dto);
        Producto saved = productoService.guardar(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Actualizar producto")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<ProductoResponseDTO> update(@PathVariable Integer id,
                                                       @RequestBody ProductoRequestDTO dto) {
        Producto p = productoService.buscarPorId(id);
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStockDisponible(dto.getStockDisponible());
        resolveRelations(p, dto);
        Producto saved = productoService.actualizar(id, p);
        return ResponseEntity.ok(productoMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPRENDEDOR','ADMIN')")
    @Operation(summary = "Eliminar producto")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void resolveRelations(Producto p, ProductoRequestDTO dto) {
        if (dto.getIdEmprendimiento() != null)
            p.setEmprendimiento(emprendimientoRepository.findById(dto.getIdEmprendimiento())
                    .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado: " + dto.getIdEmprendimiento())));
        if (dto.getIdEstado() != null)
            p.setEstado(estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + dto.getIdEstado())));
    }
}
