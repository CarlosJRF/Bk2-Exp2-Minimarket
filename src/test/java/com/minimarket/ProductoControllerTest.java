package com.minimarket;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import com.minimarket.controller.ProductoController;
import java.util.Arrays;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Bebida Cola 2L");
        producto.setPrecio(2500.0);
        producto.setStock(100);
    }

    @Test
    void testListarProductos() throws Exception {
        when(productoService.findAll()).thenReturn(Arrays.asList(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Bebida Cola 2L"));
    }

    @Test
    void testEliminarProductoExitoso() throws Exception {
        when(productoService.findById(1L)).thenReturn(producto);
        // doNothing() se usa para métodos void como deleteById
        doNothing().when(productoService).deleteById(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent()); // Status 204
    }
}