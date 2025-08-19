package com.alejandro.microservices.notifications.controller;

import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 👤 Crear un nuevo usuario
    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.createUser(username, password, role));
    }

    // 📋 Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 🔍 Buscar usuario por username
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🗑️ Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
