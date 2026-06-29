package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.DetalleVentaController;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.DetalleVentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DetalleVentaController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser(authorities = "ADMIN") // Usamos ADMIN para asegurar acceso total en las pruebas
public class DetalleVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DetalleVentaService detalleVentaService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService; // Requerido para el contexto de seguridad

    @Autowired
    private ObjectMapper objectMapper;

    private DetalleVenta detalleVenta;

    @BeforeEach
    void setUp() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Galletas");

        detalleVenta = new DetalleVenta();
        detalleVenta.setId(1L);
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(3);
        detalleVenta.setPrecio(1500.0);
    }

    // 1. Prueba para listarDetalleVentas()
    @Test
    void testListarDetalleVentas() throws Exception {
        when(detalleVentaService.findAll()).thenReturn(Arrays.asList(detalleVenta));

        mockMvc.perform(get("/api/detalle-ventas")) // Asegúrate de que tu RequestMapping sea este
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(3))
                .andExpect(jsonPath("$[0].precio").value(1500.0));
    }

    // 2. Pruebas para obtenerDetalleVentaPorId(Long)
    @Test
    void testObtenerDetalleVentaPorId_Encontrado() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalleVenta);

        mockMvc.perform(get("/api/detalle-ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidad").value(3));
    }

    @Test
    void testObtenerDetalleVentaPorId_NoEncontrado() throws Exception {
        when(detalleVentaService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/detalle-ventas/99"))
                .andExpect(status().isNotFound()); // Cubre la rama del if (NotFound)
    }

    // 3. Prueba para guardarDetalleVenta(DetalleVenta)
    @Test
    void testGuardarDetalleVenta() throws Exception {
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        mockMvc.perform(post("/api/detalle-ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalleVenta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // 4. Pruebas para actualizarDetalleVenta(Long, DetalleVenta)
    @Test
    void testActualizarDetalleVenta_Exito() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalleVenta);
        when(detalleVentaService.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        mockMvc.perform(put("/api/detalle-ventas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalleVenta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(3));
    }

    @Test
    void testActualizarDetalleVenta_NoEncontrado() throws Exception {
        when(detalleVentaService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/detalle-ventas/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(detalleVenta)))
                .andExpect(status().isNotFound()); // Cubre la rama cuando se intenta actualizar algo que no existe
    }

    // 5. Pruebas para eliminarDetalleVenta(Long)
    @Test
    void testEliminarDetalleVenta_Exito() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalleVenta);
        doNothing().when(detalleVentaService).deleteById(1L);

        mockMvc.perform(delete("/api/detalle-ventas/1"))
                .andExpect(status().isNoContent()); // Retorna 204 No Content
        
        verify(detalleVentaService, times(1)).deleteById(1L); // Verifica que el servicio de borrado fue llamado
    }

    @Test
    void testEliminarDetalleVenta_NoEncontrado() throws Exception {
        when(detalleVentaService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/detalle-ventas/99"))
                .andExpect(status().isNotFound());
        
        verify(detalleVentaService, never()).deleteById(anyLong()); // Verifica que NO se llamó al borrado
    }
}