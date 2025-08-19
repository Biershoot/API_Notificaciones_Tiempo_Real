package com.alejandro.microservices.notifications.service;

import com.alejandro.microservices.notifications.model.User;
import com.alejandro.microservices.notifications.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 👤 Crear un nuevo usuario
    public User createUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = User.builder()
                .username(username)
                .password(password) // En producción debería estar encriptada
                .role(role)
                .build();

        return userRepository.save(user);
    }

    // 🔍 Buscar usuario por username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 📋 Listar todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🗑️ Eliminar usuario
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
