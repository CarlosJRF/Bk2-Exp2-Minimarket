package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Control y registro de movimientos de stock con HATEOAS")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Listar movimientos de inventario", description = "Obtiene el historial completo de movimientos de stock con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Historial listado exitosamente")
    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {
        List<EntityModel<Inventario>> inventarios = inventarioService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(inventarios,
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withSelfRel());
    }

    @Operation(summary = "Obtener movimiento por ID", description = "Busca un movimiento específico e incluye vínculos al producto asociado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento localizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Movimiento de inventario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(@Parameter(description = "ID del movimiento de inventario") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            return ResponseEntity.ok(toModel(inventario));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar movimiento de stock", description = "Añade una nueva entrada o salida en el sistema. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento guardado de forma correcta"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios insuficientes")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public EntityModel<Inventario> registrarMovimiento(@Valid @RequestBody Inventario inventario) {
        Inventario guardado = inventarioService.save(inventario);
        return toModel(guardado);
    }

    @Operation(summary = "Actualizar movimiento", description = "Modifica los datos de un registro de stock. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado para actualizar"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<Inventario>> actualizarMovimiento(@Parameter(description = "ID del movimiento a modificar") @Valid @PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            Inventario actualizado = inventarioService.save(inventario);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar movimiento del historial", description = "Remueve un registro del inventario. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Movimiento no localizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarMovimiento(@Parameter(description = "ID del registro a eliminar") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EntityModel<Inventario> toModel(Inventario inventario) {
        EntityModel<Inventario> model = EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withRel("inventario"));

        if (inventario.getProducto() != null && inventario.getProducto().getId() != null) {
            model.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(inventario.getProducto().getId())).withRel("producto"));
        }

        return model;
    }
}