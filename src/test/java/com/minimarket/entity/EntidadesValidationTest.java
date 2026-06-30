package com.minimarket.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class EntidadesValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        // Configuramos el validador manual para usarlo en las pruebas
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testProductoValidacion_Correcto_E_Incorrecto() {
        Producto producto = new Producto();
        producto.setId(1L);
        
        // 1. Probamos setters y getters básicos
        producto.setNombre("Galletas");
        assertEquals("Galletas", producto.getNombre());
        
        // 2. Probamos reglas inválidas (Precio negativo y stock negativo)
        producto.setPrecio(-500.0);
        producto.setStock(-10);
        
        Set<ConstraintViolation<Producto>> violaciones = validator.validate(producto);
        
        // Esperamos 2 errores (precio y stock negativos)
        assertFalse(violaciones.isEmpty());
        assertEquals(2, violaciones.size());

        // 3. Probamos reglas válidas
        producto.setPrecio(1500.0);
        producto.setStock(50);
        
        violaciones = validator.validate(producto);
        assertTrue(violaciones.isEmpty()); // 0 errores
    }

    @Test
    void testCategoriaValidacion() {
        Categoria categoria = new Categoria();
        
        // Nombre en blanco debe fallar
        categoria.setNombre("");
        Set<ConstraintViolation<Categoria>> violaciones = validator.validate(categoria);
        assertFalse(violaciones.isEmpty());
        
        // Nombre válido debe pasar
        categoria.setNombre("Lácteos");
        violaciones = validator.validate(categoria);
        assertTrue(violaciones.isEmpty());
        assertEquals("Lácteos", categoria.getNombre());
    }

    @Test
    void testDetalleVentaValidacion() {
        DetalleVenta detalle = new DetalleVenta();
        
        // Cantidad 0 debe fallar (mínimo es 1)
        detalle.setCantidad(0);
        detalle.setPrecio(-100.0); // Precio negativo debe fallar
        
        Set<ConstraintViolation<DetalleVenta>> violaciones = validator.validate(detalle);
        assertEquals(2, violaciones.size());
        
        // Valores válidos
        detalle.setCantidad(2);
        detalle.setPrecio(2000.0);
        violaciones = validator.validate(detalle);
        assertTrue(violaciones.isEmpty());
    }

    @Test
    void testVentaValidacion() {
        Venta venta = new Venta();
        venta.setId(1L);
        
        // Validamos Getters/Setters y validación nula
        venta.setFecha(null);
        Set<ConstraintViolation<Venta>> violaciones = validator.validate(venta);
        assertFalse(violaciones.isEmpty()); // Falla porque fecha es NotNull
        
        Date ahora = new Date();
        venta.setFecha(ahora);
        violaciones = validator.validate(venta);
        assertTrue(violaciones.isEmpty());
        assertEquals(ahora, venta.getFecha());
    }

    @Test
    void testRolValidacion() {
        Rol rol = new Rol("Test");
        
        rol.setNombre("   "); // Espacios en blanco
        Set<ConstraintViolation<Rol>> violaciones = validator.validate(rol);
        assertFalse(violaciones.isEmpty()); // Falla por NotBlank
        
        rol.setNombre("ADMIN");
        violaciones = validator.validate(rol);
        assertTrue(violaciones.isEmpty());
        assertEquals("ADMIN", rol.getNombre());
    }
}