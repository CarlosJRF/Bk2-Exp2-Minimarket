package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.InventarioController;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.InventarioService;
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
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser(authorities = "ADMIN")
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService inventarioService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

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

    // Pruebas Nuevas de Cobertura
    @Test
    void testListarMovimientosDeInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(Arrays.asList(inventario));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(20));
    }

    @Test
    void testObtenerMovimientoPorId_Encontrado() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventario);

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("Entrada"));
    }

    @Test
    void testObtenerMovimientoPorId_NoEncontrado() throws Exception {
        when(inventarioService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarMovimiento_Exito() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventario);
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(put("/api/inventario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(20));
    }

    @Test
    void testActualizarMovimiento_NoEncontrado() throws Exception {
        when(inventarioService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/inventario/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarMovimiento_Exito() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventario);
        doNothing().when(inventarioService).deleteById(1L);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarMovimiento_NoEncontrado() throws Exception {
        when(inventarioService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }

    // Pruebas Originales de Seguridad
    @Test
    void testRegistrarMovimiento_ComoAdmin_Exitoso() throws Exception {
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "CLIENTE") // Sobrescribe el rol de la clase
    void testRegistrarMovimiento_ComoCliente_Denegado() throws Exception {
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());
    }
}