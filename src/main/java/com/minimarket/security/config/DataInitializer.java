package com.minimarket.security.config;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, 
                                      RolRepository rolRepository, 
                                      CategoriaRepository categoriaRepository,
                                      ProductoRepository productoRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("=========================================================");
            System.out.println("--- VERIFICANDO Y CREANDO DATOS DE PRUEBA EN BD ---");

            // 1. Verificar y Sembrar Roles y Usuarios
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                Rol rolAdmin = new Rol("ADMIN");
                rolRepository.save(rolAdmin);

                Rol rolCajero = new Rol("CAJERO");
                rolRepository.save(rolCajero);

                Rol rolCliente = new Rol("CLIENTE");
                rolRepository.save(rolCliente);

                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                Set<Rol> rolesAdmin = new HashSet<>();
                rolesAdmin.add(rolAdmin);
                admin.setRoles(rolesAdmin);
                usuarioRepository.save(admin);

                System.out.println("-> ¡Usuario 'admin' (ID: 1) creado exitosamente!");
            }

            // 2. Verificar y Sembrar Categoría y Producto Base
            if (categoriaRepository.findAll().isEmpty()) {
                // Creamos la Categoría ID: 1
                Categoria catAbarrotes = new Categoria();
                catAbarrotes.setNombre("Abarrotes y Snacks");
                categoriaRepository.save(catAbarrotes);
                System.out.println("-> ¡Categoría 'Abarrotes y Snacks' (ID: 1) creada!");

                // Creamos el Producto ID: 1 vinculado a la Categoría 1
                Producto prodBase = new Producto();
                prodBase.setNombre("Papas Fritas Corte Casero 250g");
                prodBase.setPrecio(1850.0);
                prodBase.setStock(100);
                prodBase.setCategoria(catAbarrotes);
                productoRepository.save(prodBase);
                System.out.println("-> ¡Producto Base (ID: 1) creado e inyectado!");
            }

            System.out.println("=========================================================");
        };
    }
}