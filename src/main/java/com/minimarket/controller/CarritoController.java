package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito de Compras", description = "Operaciones para gestionar los productos añadidos al carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Listar carritos", description = "Obtiene una lista con todos los registros del carrito de compras.")
    @ApiResponse(responseCode = "200", description = "Lista de carritos obtenida correctamente")
    @GetMapping
    public List<Carrito> listarCarrito() {
        return carritoService.findAll();
    }

    @Operation(summary = "Obtener carrito por ID", description = "Busca un registro específico en el carrito utilizando su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(@Parameter(description = "ID del carrito") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return (carrito != null) ? ResponseEntity.ok(carrito) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Agregar producto al carrito", description = "Crea un nuevo registro en el carrito de compras.")
    @ApiResponse(responseCode = "200", description = "Producto agregado al carrito con éxito")
    @PostMapping
    public Carrito agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        return carritoService.save(carrito);
    }

    @Operation(summary = "Actualizar carrito", description = "Modifica un registro existente en el carrito de compras.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado para actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@Parameter(description = "ID del carrito a actualizar") @Valid @PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar del carrito", description = "Elimina un registro específico del carrito de compras.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Registro eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@Parameter(description = "ID del carrito a eliminar") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}