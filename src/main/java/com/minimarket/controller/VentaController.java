package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
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
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Procesamiento de transacciones comerciales de la caja del minimarket")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Operation(summary = "Listar todas las ventas", description = "Obtiene el registro histórico de todas las boletas o transacciones comerciales efectuadas.")
    @ApiResponse(responseCode = "200", description = "Ventas recuperadas con éxito")
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @Operation(summary = "Obtener una venta por ID", description = "Busca el detalle técnico de una boleta de venta específica y sus líneas de productos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta localizada"),
            @ApiResponse(responseCode = "404", description = "Número de boleta/venta no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@Parameter(description = "ID o número correlativo de la venta") @PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar nueva transacción de venta", description = "Genera el cierre de una venta y procesa sus artículos. Requiere rol CAJERO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta procesada e ingresada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Endpoint exclusivo para personal de caja CAJERO")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CAJERO')")
    public Venta guardarVenta(@Valid @RequestBody Venta venta) {
        return ventaService.save(venta);
    }
}