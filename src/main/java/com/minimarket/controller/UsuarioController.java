package com.minimarket.controller;

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Administración general de usuarios y sus perfiles hipermedia")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios", description = "Recupera la lista de todas las cuentas con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos con éxito")
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<EntityModel<Usuario>> usuarios = usuarioService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca los datos de perfil de un usuario e incluye enlaces a sus operaciones posibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado de forma exitosa"),
            @ApiResponse(responseCode = "404", description = "Usuario no registrado en el sistema")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obtenerUsuarioPorId(@Parameter(description = "ID de la cuenta de usuario") @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findById(id);
        return usuario.map(u -> ResponseEntity.ok(toModel(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta nueva en el sistema.")
    @ApiResponse(responseCode = "200", description = "Usuario creado y guardado satisfactoriamente")
    @PostMapping
    public EntityModel<Usuario> guardarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario guardado = usuarioService.save(usuario);
        return toModel(guardado);
    }

    @Operation(summary = "Actualizar datos de usuario", description = "Permite modificar los atributos de seguridad o perfiles de una cuenta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no localizado para modificar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@Parameter(description = "ID del usuario a modificar") @PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioService.findById(id);
        if (usuarioExistente.isPresent()) {
            usuario.setId(id);
            Usuario actualizado = usuarioService.save(usuario);
            return ResponseEntity.ok(toModel(actualizado));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una cuenta de usuario", description = "Remueve un usuario del sistema mediante borrado físico por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
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

    private EntityModel<Usuario> toModel(Usuario usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withRel("usuarios"),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos"),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas"));
    }
}