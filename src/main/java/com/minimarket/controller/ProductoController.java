package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos disponibles con soporte HATEOAS")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listar todos los productos", description = "Muestra el catálogo completo de productos envueltos en un modelo de hipermedia.")
    @ApiResponse(responseCode = "200", description = "Productos listados exitosamente con enlaces HATEOAS")
    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca los datos de un producto específico e incluye enlaces de navegación y relaciones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado con enlaces HATEOAS"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(@Parameter(description = "ID del producto") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            return ResponseEntity.ok(toModel(producto));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear producto", description = "Añade un nuevo producto al sistema y retorna el recurso con hipervínculos. Requiere rol ADMIN.")
    @ApiResponse(responseCode = "200", description = "Producto guardado correctamente")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public EntityModel<Producto> guardarProducto(@Valid @RequestBody Producto producto) {
        Producto guardado = productoService.save(producto);
        return toModel(guardado);
    }

    @Operation(summary = "Actualizar producto", description = "Modifica los datos de un producto existente. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<Producto>> actualizarProducto(@Parameter(description = "ID del producto") @Valid @PathVariable Long id, @RequestBody Producto producto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente != null) {
            producto.setId(id);
            Producto actualizado = productoService.save(producto);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar producto", description = "Remueve un producto del catálogo. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@Parameter(description = "ID del producto") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Método auxiliar para construir el EntityModel con sus enlaces (DRY Principle)
    private EntityModel<Producto> toModel(Producto producto) {
        EntityModel<Producto> model = EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"));

        // Enlace condicional a la Categoría relacionada
        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            model.add(linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(producto.getCategoria().getId())).withRel("categoria"));
        }
        
        // Enlace cruzado al módulo de Inventario
        model.add(linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withRel("inventario"));

        return model;
    }
}