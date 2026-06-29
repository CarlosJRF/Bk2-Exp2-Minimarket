package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

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
    void testFindAll() {
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));
        
        List<Producto> resultado = productoService.findAll();
        
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Bebida Cola 2L", resultado.get(0).getNombre());
    }

    @Test
    void testFindById_Encontrado() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        
        Producto resultado = productoService.findById(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Bebida Cola 2L", resultado.getNombre());
    }

    @Test
    void testFindById_NoEncontrado() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        
        Producto resultado = productoService.findById(99L);
        
        assertNull(resultado);
    }

    @Test
    void testSave() {
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        
        Producto resultado = productoService.save(producto);
        
        assertNotNull(resultado);
        assertEquals(2500.0, resultado.getPrecio());
    }

    @Test
    void testDeleteById() {
        doNothing().when(productoRepository).deleteById(1L);
        
        productoService.deleteById(1L);
        
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByCategoriaId() {
        when(productoRepository.findByCategoriaId(1L)).thenReturn(Arrays.asList(producto));
        
        List<Producto> resultado = productoService.findByCategoriaId(1L);
        
        assertFalse(resultado.isEmpty());
        assertEquals(1L, resultado.get(0).getId());
    }
}
