package com.alejandro.microservices.notifications.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

/**
 * Entidad que representa un usuario del sistema.
 * Contiene información básica de autenticación y autorización.
 */
@Entity
@Table(name = "users")
@Schema(description = "Entidad que representa un usuario del sistema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre de usuario único", example = "user123", required = true)
    private String username;

    @Column(nullable = false)
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String password;

    @Schema(description = "Rol del usuario en el sistema", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role; // Ejemplo: USER, ADMIN

    /**
     * Constructor por defecto requerido por JPA.
     */
    public User() {
    }

    /**
     * Constructor con todos los campos.
     *
     * @param id ID del usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @param role Rol del usuario
     */
    public User(Long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
