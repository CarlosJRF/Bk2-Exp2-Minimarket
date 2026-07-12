package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Control y registro de movimientos de stock (Entradas/Salidas)")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Listar movimientos de inventario", description = "Obtiene el historial completo de movimientos de stock.")
    @ApiResponse(responseCode = "200", description = "Historial listado exitosamente")
    @GetMapping
    public List<Inventario> listarMovimientosDeInventario() {
        return inventarioService.findAll();
    }

    @Operation(summary = "Obtener movimiento por ID", description = "Busca un movimiento de inventario específico utilizando su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento localizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Movimiento de inventario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerMovimientoPorId(@Parameter(description = "ID del movimiento de inventario") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null) ? ResponseEntity.ok(inventario) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar movimiento de stock", description = "Añade una nueva entrada o salida de un producto en el sistema. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento guardado de forma correcta"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Privilegios insuficientes")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Inventario registrarMovimiento(@Valid @RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }

    @Operation(summary = "Actualizar movimiento", description = "Modifica los datos de un registro de stock existente. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado para actualizar"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Inventario> actualizarMovimiento(@Parameter(description = "ID del movimiento a modificar") @Valid @PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioService.save(inventario));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar movimiento del historial", description = "Remueve permanentemente un registro del inventario. Requiere rol ADMIN.")
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
}