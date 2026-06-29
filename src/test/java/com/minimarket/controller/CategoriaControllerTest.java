package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.controller.CategoriaController;
import com.minimarket.entity.Categoria;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.CategoriaService;
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

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@WithMockUser(authorities = "ADMIN")
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");
    }

    @Test
    void testListarCategorias() throws Exception {
        when(categoriaService.findAll()).thenReturn(Arrays.asList(categoria));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Abarrotes"));
    }

    @Test
    void testObtenerCategoriaPorId_Encontrado() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(categoria);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Abarrotes"));
    }

    @Test
    void testObtenerCategoriaPorId_NoEncontrado() throws Exception {
        when(categoriaService.findById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGuardarCategoria() throws Exception {
        when(categoriaService.save(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testActualizarCategoria_Exito() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(categoria);
        when(categoriaService.save(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(put("/api/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Abarrotes"));
    }

    @Test
    void testActualizarCategoria_NoEncontrado() throws Exception {
        when(categoriaService.findById(99L)).thenReturn(null);

        mockMvc.perform(put("/api/categorias/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarCategoria_Exito() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(categoria);
        doNothing().when(categoriaService).deleteById(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testEliminarCategoria_NoEncontrado() throws Exception {
        when(categoriaService.findById(99L)).thenReturn(null);

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound());
    }
}