package com.minimarket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.minimarket.controller.VentaController;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Venta venta;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Inicializamos el usuario requerido por la entidad Venta
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUsername("cliente_vip");

        venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
    }

    @Test
    void testGuardarVenta() throws Exception {
        when(ventaService.save(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk())
                // Aserciones corregidas: Buscamos campos que SÍ existen en la entidad
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuario.username").value("cliente_vip"));
    }
}