package com.minimarket;


// Importaciones de JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
// Importaciones de las entidades del proyecto
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Carrito;

public class CarritoTest {

    // Variables globales para la prueba
    private Carrito carrito;
    private Usuario usuario;
    private Producto producto;

    // @BeforeEach - Preparación del entorno
    @BeforeEach
    void setUp() {
        // Instanciamos las entidades requeridas para evitar un NullPointerException.
        carrito = new Carrito();
        usuario = new Usuario();
        producto = new Producto();

        // Configuramos un usuario de prueba
        usuario.setId(1L);
        usuario.setUsername("cliente_frecuente");

        // Configuramos un producto de prueba con stock inicial
        producto.setId(100L);
        producto.setNombre("Café en grano");
        producto.setStock(50);
        producto.setPrecio(5500.0);
    }

    // Declaración de la prueba unitaria
    @Test
    void testAsignacionUsuarioYProductoAlCarrito() {
        // Continuación del Concepto 2: Arrange (Preparar)
        Integer cantidadComprada = 3;

        // Ejecutamos el comportamiento de la entidad. Asignamos los objetos y la cantidad.
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(cantidadComprada);

        // Comprobamos rigurosamente que los datos guardados en el Carrito 
        // correspondan exactamente a lo que ingresamos.
        
        //Validamos que los objetos no sean nulos
        assertNotNull(carrito.getUsuario(), "El usuario asignado no debe ser nulo");
        assertNotNull(carrito.getProducto(), "El producto asignado no debe ser nulo");
        
        // Validamos la integridad de los datos relacionados
        assertEquals("cliente_frecuente", carrito.getUsuario().getUsername(), "El username debe coincidir con el asignado");
        assertEquals("Café en grano", carrito.getProducto().getNombre(), "El nombre del producto debe coincidir");
        
        // Validamos la asignación de valores primitivos/envoltorios
        assertEquals(3, carrito.getCantidad(), "La cantidad en el carrito debe ser exactamente 3");
    }
}