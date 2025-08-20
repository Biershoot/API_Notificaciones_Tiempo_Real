package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para crear, listar, buscar y eliminar usuarios del sistema.
 *
 * @author Alejandro
 * @version 2.0
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 👤 Crear un nuevo usuario
    @PostMapping
    @Operation(
        summary = "Crear un nuevo usuario",
        description = "Registra un nuevo usuario en el sistema con username único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "El usuario ya existe o parámetros inválidos")
    })
    public ResponseEntity<User> createUser(
            @Parameter(description = "Nombre de usuario único", required = true)
            @RequestParam String username,
            @Parameter(description = "Contraseña del usuario", required = true)
            @RequestParam String password,
            @Parameter(description = "Rol del usuario (USER, ADMIN)", required = true)
            @RequestParam String role) {
        return ResponseEntity.ok(userService.createUser(username, password, role));
    }

    // 📋 Listar todos los usuarios
    @GetMapping
    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Obtiene la lista completa de usuarios registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 🔍 Buscar usuario por username
    @GetMapping("/{username}")
    @Operation(
        summary = "Buscar usuario por nombre",
        description = "Busca y obtiene un usuario específico por su nombre de usuario"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Nombre de usuario a buscar", required = true)
            @PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🗑️ Eliminar usuario
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina permanentemente un usuario del sistema por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
