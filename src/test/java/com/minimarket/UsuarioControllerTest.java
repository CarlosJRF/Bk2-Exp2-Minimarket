package com.minimarket;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import com.minimarket.controller.UsuarioController;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("admin");
    }

    @Test
    void testObtenerUsuarioPorId() throws Exception {
        //Envolvemos el mock en un Optional
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void testObtenerUsuarioNoEncontrado() throws Exception {
        // Simulamos un Optional vacío
        when(usuarioService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }
}