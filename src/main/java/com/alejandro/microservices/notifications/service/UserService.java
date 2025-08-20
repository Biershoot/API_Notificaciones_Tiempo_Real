package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de usuarios en el sistema.
 * Proporciona operaciones básicas de CRUD para usuarios.
 *
 * @author Alejandro
 * @version 2.0
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param userRepository Repositorio de usuarios
     */
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param username Nombre de usuario único
     * @param password Contraseña del usuario
     * @param role Rol del usuario (USER, ADMIN)
     * @return El usuario creado
     * @throws RuntimeException si el usuario ya existe
     */
    public User createUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Usando el constructor en lugar del patrón builder
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar
     * @return Optional con el usuario si existe, empty si no existe
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Lista todos los usuarios del sistema.
     *
     * @return Lista de todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param userId ID del usuario a eliminar
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
