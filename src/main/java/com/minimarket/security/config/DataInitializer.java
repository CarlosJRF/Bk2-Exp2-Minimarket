package com.minimarket.security.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
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
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("=========================================================");
            System.out.println("--- VERIFICANDO USUARIOS DE PRUEBA EN BASE DE DATOS ---");

            // Verificamos específicamente si el usuario "admin" existe
            if (usuarioRepository.findByUsername("admin").isEmpty()) {
                
                // 1. Crear y guardar Rol ADMIN
                Rol rolAdmin = new Rol("ADMIN");
                rolRepository.save(rolAdmin);

                // 2. Crear Usuario Administrador con contraseña encriptada (BCrypt)
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // <-- Encriptación vital
                
                Set<Rol> rolesAdmin = new HashSet<>();
                rolesAdmin.add(rolAdmin);
                admin.setRoles(rolesAdmin);
                
                usuarioRepository.save(admin);

                System.out.println("-> ¡Usuario 'admin' creado exitosamente con BCrypt!");
            } else {
                System.out.println("-> El usuario 'admin' ya se encuentra registrado en la BD.");
            }
            System.out.println("=========================================================");
        };
    }
}