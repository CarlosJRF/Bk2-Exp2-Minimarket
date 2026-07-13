package com.minimarket.security.service;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(">>> [DEBUG SECURITY] Postman intentando loguear con el usuario: '" + username + "'");

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println(">>> [DEBUG ERROR] ¡El usuario '" + username + "' NO EXISTE en la tabla USUARIO de H2!");
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        System.out.println(">>> [DEBUG ÉXITO] ¡Usuario '" + username + "' encontrado en BD!");
        System.out.println(">>> [DEBUG DATOS] Contraseña encriptada en BD: " + usuario.getPassword());
        System.out.println(">>> [DEBUG DATOS] Roles asignados: " + usuario.getRoles());

        return new CustomUserDetails(usuario);
    }
}
