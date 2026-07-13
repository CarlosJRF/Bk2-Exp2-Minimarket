package com.minimarket.controller;

import com.minimarket.security.model.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y gestión de acceso con persistencia de sesión")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario utilizando sus credenciales y establece una sesión HTTP segura y persistente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa y sesión establecida"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // 1. Autenticamos las credenciales en la base de datos
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), 
                            loginRequest.getPassword()
                    )
            );

            // 2. Establecemos la autenticación en el contexto de seguridad de Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Guardamos explícitamente el contexto en la sesión HTTP
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return ResponseEntity.ok("Autenticación exitosa y sesión iniciada para el usuario: " + loginRequest.getUsername());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}