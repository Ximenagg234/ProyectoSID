package edu.icesi.emprendimientos.rest.controller;

import edu.icesi.emprendimientos.entity.*;
import edu.icesi.emprendimientos.repository.*;
import edu.icesi.emprendimientos.rest.dto.PedidoRequestDTO;
import edu.icesi.emprendimientos.rest.dto.PedidoResponseDTO;
import edu.icesi.emprendimientos.rest.mapper.PedidoMapper;
import edu.icesi.emprendimientos.service.PedidoService;
import edu.icesi.emprendimientos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "CRUD de pedidos")
@SecurityRequirement(name = "BearerAuth")
public class RestPedidoController {

    private final PedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final UsuarioService usuarioService;
    private final EmprendimientoRepository emprendimientoRepository;
    private final EstadoRepository estadoRepository;
    private final ProductoRepository productoRepository;

    public RestPedidoController(PedidoService pedidoService,
                                 PedidoMapper pedidoMapper,
                                 UsuarioService usuarioService,
                                 EmprendimientoRepository emprendimientoRepository,
                                 EstadoRepository estadoRepository,
                                 ProductoRepository productoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoMapper = pedidoMapper;
        this.usuarioService = usuarioService;
        this.emprendimientoRepository = emprendimientoRepository;
        this.estadoRepository = estadoRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Éxito")
    public ResponseEntity<List<PedidoResponseDTO>> getAll() {
        return ResponseEntity.ok(pedidoService.listar().stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    @ApiResponse(responseCode = "200", description = "Éxito")
    @ApiResponse(responseCode = "404", description = "No encontrado")
    public ResponseEntity<PedidoResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoMapper.toDto(pedidoService.buscarPorId(id)));
    }

    @GetMapping("/mis-pedidos")
    @Operation(summary = "Ver mis pedidos (comprador autenticado)")
    @ApiResponse(responseCode = "200", description = "Éxito")
    public ResponseEntity<List<PedidoResponseDTO>> getMisPedidos(Authentication auth) {
        Usuario comprador = usuarioService.buscarPorCorreo(auth.getName());
        return ResponseEntity.ok(pedidoService.listarPorUsuario(comprador.getIdUsuario()).stream()
                .map(pedidoMapper::toDto)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @Operation(summary = "Crear pedido")
    @ApiResponse(responseCode = "201", description = "Creado")
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setFechaPedido(new Date());

        Usuario usuario = usuarioService.buscarPorId(dto.getIdUsuario());
        pedido.setUsuario(usuario);

        Emprendimiento emp = emprendimientoRepository.findById(dto.getIdEmprendimiento())
                .orElseThrow(() -> new EntityNotFoundException("Emprendimiento no encontrado"));
        pedido.setEmprendimiento(emp);

        Estado estado = estadoRepository.findById(dto.getIdEstado())
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado"));
        pedido.setEstado(estado);

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getDetalles() != null) {
            for (var d : dto.getDetalles()) {
                DetallePedido det = new DetallePedido();
                Producto prod = productoRepository.findById(d.getIdProducto())
                        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
                det.setProducto(prod);
                det.setCantidad(d.getCantidad());
                det.setPrecioUnitario(d.getPrecioUnitario());
                det.setSubtotal(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
                det.setPedido(pedido);
                detalles.add(det);
                total = total.add(det.getSubtotal());
            }
        }
        pedido.setDetalles(detalles);
        pedido.setTotal(total);

        Pedido saved = pedidoService.crearPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pedido")
    @ApiResponse(responseCode = "200", description = "Actualizado")
    public ResponseEntity<PedidoResponseDTO> update(@PathVariable Integer id,
                                                     @RequestBody PedidoRequestDTO dto) {
        Pedido pedido = pedidoService.buscarPorId(id);
        if (dto.getIdEstado() != null) {
            Estado estado = estadoRepository.findById(dto.getIdEstado())
                    .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado"));
            pedido.setEstado(estado);
        }
        pedidoService.actualizarEstado(id, dto.getIdEstado());
        return ResponseEntity.ok(pedidoMapper.toDto(pedidoService.buscarPorId(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido")
    @ApiResponse(responseCode = "204", description = "Eliminado")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
