package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.InventarioController;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.service.InventarioService;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;

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

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
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

    // TEST B.1: Registro de movimiento permitido para ADMIN
    @Test
    @WithMockUser(authorities = "ADMIN")
    void testRegistrarMovimiento_ComoAdmin_Exitoso() throws Exception {
        when(inventarioService.save(any(Inventario.class))).thenReturn(inventario);

        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoMovimiento").value("Entrada"));
    }

    // TEST B.2: Registro de movimiento denegado para usuario sin rol explícito
    @Test
    @WithMockUser(authorities = "CLIENTE")
    void testRegistrarMovimiento_ComoCliente_Denegado() throws Exception {
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isForbidden());
    }
}