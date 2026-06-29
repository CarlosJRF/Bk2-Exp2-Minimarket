package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.CarritoController;
import com.minimarket.entity.Carrito;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.CarritoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser(authorities = "CLIENTE")
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Carrito carrito;

    @BeforeEach
    void setUp() {
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setCantidad(5);
    }

    @Test
    void testListarCarrito() throws Exception {
        when(carritoService.findAll()).thenReturn(Arrays.asList(carrito));

        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(5));
    }

    @Test
    void testObtenerCarritoPorId_NoEncontrado() throws Exception {
        when(carritoService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/carrito/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarProductoDelCarrito() throws Exception {
        when(carritoService.findById(1L)).thenReturn(carrito);
        doNothing().when(carritoService).deleteById(1L);

        mockMvc.perform(delete("/api/carrito/1"))
                .andExpect(status().isNoContent());
    }
}