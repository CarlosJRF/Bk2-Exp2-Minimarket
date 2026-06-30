package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.service.impl.DetalleVentaServiceImpl;
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
public class DetalleVentaServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private DetalleVentaServiceImpl detalleVentaService;

    private DetalleVenta detalleVenta;

    @BeforeEach
    void setUp() {
        detalleVenta = new DetalleVenta();
        detalleVenta.setId(1L);
        detalleVenta.setCantidad(2);
        detalleVenta.setPrecio(1500.0);
    }

    @Test
    void testFindAll() {
        when(detalleVentaRepository.findAll()).thenReturn(Arrays.asList(detalleVenta));
        
        List<DetalleVenta> resultado = detalleVentaService.findAll();
        
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).getCantidad());
    }

    @Test
    void testFindById_Encontrado() {
        // En los servicios, el repositorio suele retornar un Optional
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalleVenta));
        
        DetalleVenta resultado = detalleVentaService.findById(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testFindById_NoEncontrado() {
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());
        
        DetalleVenta resultado = detalleVentaService.findById(99L);
        
        assertNull(resultado); // Tu servicio probablemente retorna null si no lo encuentra por el orElse(null)
    }

    @Test
    void testSave() {
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);
        
        DetalleVenta resultado = detalleVentaService.save(detalleVenta);
        
        assertNotNull(resultado);
        assertEquals(1500.0, resultado.getPrecio());
    }

    @Test
    void testDeleteById() {
        doNothing().when(detalleVentaRepository).deleteById(1L);
        
        detalleVentaService.deleteById(1L);
        
        // Verificamos que el repositorio efectivamente fue llamado 1 vez con el ID 1
        verify(detalleVentaRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindByVentaId() {
        when(detalleVentaRepository.findByVentaId(1L)).thenReturn(Arrays.asList(detalleVenta));
        
        List<DetalleVenta> resultado = detalleVentaService.findByVentaId(1L);
        
        assertFalse(resultado.isEmpty());
        assertEquals(1L, resultado.get(0).getId());
    }
}