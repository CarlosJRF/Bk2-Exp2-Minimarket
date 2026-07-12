package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito de Compras", description = "Operaciones para gestionar los productos añadidos al carrito con hipermedia")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Listar carritos", description = "Obtiene una lista con todos los registros del carrito de compras en formato HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Lista de carritos obtenida correctamente")
    @GetMapping
    public CollectionModel<EntityModel<Carrito>> listarCarrito() {
        List<EntityModel<Carrito>> carritos = carritoService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(carritos,
                linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());
    }

    @Operation(summary = "Obtener carrito por ID", description = "Busca un registro específico en el carrito e incluye enlaces al producto y usuario vinculados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado con hipermedia"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(@Parameter(description = "ID del carrito") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            return ResponseEntity.ok(toModel(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Agregar producto al carrito", description = "Crea un nuevo registro en el carrito de compras.")
    @ApiResponse(responseCode = "200", description = "Producto agregado al carrito con éxito")
    @PostMapping
    public EntityModel<Carrito> agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        Carrito guardado = carritoService.save(carrito);
        return toModel(guardado);
    }

    @Operation(summary = "Actualizar carrito", description = "Modifica un registro existente en el carrito de compras.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado para actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> actualizarCarrito(@Parameter(description = "ID del carrito a actualizar") @Valid @PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            Carrito actualizado = carritoService.save(carrito);
            return ResponseEntity.ok(toModel(actualizado));
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

    // Método auxiliar para encapsular la lógica de enlaces HATEOAS
    private EntityModel<Carrito> toModel(Carrito carrito) {
        EntityModel<Carrito> model = EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos"));

        // Enlaces relacionales al Producto y al Usuario
        if (carrito.getProducto() != null && carrito.getProducto().getId() != null) {
            model.add(linkTo(methodOn(ProductoController.class).obtenerProductoPorId(carrito.getProducto().getId())).withRel("producto"));
        }
        if (carrito.getUsuario() != null && carrito.getUsuario().getId() != null) {
            model.add(linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(carrito.getUsuario().getId())).withRel("usuario"));
        }

        return model;
    }
}