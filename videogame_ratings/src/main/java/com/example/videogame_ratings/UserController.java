package com.example.videogame_ratings;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser register(@RequestBody RegistrationRequest request) {
        // Validate role
        if (!request.role().equals("USER") && !request.role().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be either USER or ADMIN");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        // Create new user with encoded password
        AppUser newUser = new AppUser(
            request.username(),
            passwordEncoder.encode(request.password()),
            request.role()
        );

        return userRepository.save(newUser);
    }
}