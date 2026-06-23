package com.minimarket;

// Importaciones para MockMvc y JSON
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Importaciones de Mockito y utilidades
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


//import de entidades y controlador del proyecto
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.service.InventarioService;
import com.minimarket.controller.InventarioController;
import java.util.Date;

// habilitamos la prueba solo para el controlador web
@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InventarioControllerTest {

    // Inyectamos el cliente HTTP simulado
    @Autowired
    private MockMvc mockMvc;

    //Inyectamos el servicio simulado
    @MockBean
    private InventarioService inventarioService;

    //Herramienta para convertir objetos a JSON
    @Autowired
    private ObjectMapper objectMapper;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        Producto producto = new Producto();
        producto.setId(10L);
        producto.setNombre("Harina");

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(20);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
    }

    @Test
    void testObtenerMovimientoPorIdExitoso() throws Exception {
        // Configuramos el servicio para que devuelva nuestro objeto simulado
        when(inventarioService.findById(1L)).thenReturn(inventario);

        //Hacemos un GET y verificamos el status 200 y el contenido
        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("Entrada"))
                .andExpect(jsonPath("$.cantidad").value(20));
    }

    @Test
    void testObtenerMovimientoPorIdNoEncontrado() throws Exception {
        // Simulamos que el ID no existe (devuelve null)
        when(inventarioService.findById(99L)).thenReturn(null);

        // Verificamos que el controlador devuelva el status 404 (Not Found)
        mockMvc.perform(get("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegistrarMovimiento() throws Exception {
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        //Enviamos un POST con el objeto convertido a JSON
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario))) // Concepto 4 en acción
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("Entrada"));
    }
}