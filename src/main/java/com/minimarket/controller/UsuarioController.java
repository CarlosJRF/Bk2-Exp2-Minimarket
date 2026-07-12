package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administración general de usuarios, clientes y asignación de roles")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios", description = "Recupera la lista de todas las cuentas registradas en la aplicación.")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos con éxito")
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.findAll();
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca los datos de perfil de un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado de forma exitosa"),
            @ApiResponse(responseCode = "404", description = "Usuario no registrado en el sistema")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@Parameter(description = "ID de la cuenta de usuario") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta nueva en el sistema con credenciales básicas.")
    @ApiResponse(responseCode = "200", description = "Usuario creado y guardado satisfactoriamente")
    @PostMapping
    public Usuario guardarUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.save(usuario);
    }

    @Operation(summary = "Actualizar datos de usuario", description = "Permite modificar los atributos de seguridad o perfiles de una cuenta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no localizado para modificar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@Parameter(description = "ID del usuario a modificar") @PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioService.save(usuario));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una cuenta de usuario", description = "Remueve un usuario del sistema mediante borrado físico por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada en la base de datos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@Parameter(description = "ID de la cuenta a dar de baja") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}