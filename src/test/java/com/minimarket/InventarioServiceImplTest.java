package com.minimarket;

// Importaciones de JUnit 5 y aserciones
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Importaciones de Mockito
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Importaciones de Entidades
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;

//Importaciones de la clase a probar
import com.minimarket.service.impl.InventarioServiceImpl;

import java.util.Date;

//Habilitar Mockito en JUnit 5
@ExtendWith(MockitoExtension.class)
public class InventarioServiceImplTest {

    // Creación del Mock e Inyección
    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    // Variables globales
    private Inventario inventario;
    private Producto producto;

    @BeforeEach
    void setUp() {
        // Configuramos los datos simulados
        producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Galletas de Avena");

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(100);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
    }

    @Test
    void testGuardarInventarioAislado() {
        // Le decimos al Mock que cuando reciba CUALQUIER objeto Inventario para guardar, 
        // devuelva nuestra variable 'inventario' creada en el setUp().
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        //Llamamos al método del SERVICIO (la clase real)
        Inventario inventarioGuardado = inventarioService.save(inventario);

        // Validamos que los campos exigidos no sean nulos
        assertNotNull(inventarioGuardado, "El inventario guardado no debe ser nulo");
        assertNotNull(inventarioGuardado.getTipoMovimiento(), "El campo tipoMovimiento no puede ser nulo");
        assertNotNull(inventarioGuardado.getCantidad(), "El campo cantidad no puede ser nulo");
        
        // Validamos la relación correcta Producto-Inventario
        assertEquals(10L, inventarioGuardado.getProducto().getId(), "El ID del producto en el inventario debe coincidir");
        assertEquals("Entrada", inventarioGuardado.getTipoMovimiento(), "El tipo de movimiento debe ser 'Entrada'");
        assertEquals(100, inventarioGuardado.getCantidad(), "La cantidad registrada debe ser 100");

        // Comprobamos que el servicio intentó guardar en la base de datos exactamente 1 vez.
        verify(inventarioRepository, times(1)).save(inventario);
    }
}
