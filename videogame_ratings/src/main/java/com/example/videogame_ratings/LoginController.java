package com.example.videogame_ratings;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class LoginController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;

    static final String PREFIX = "Bearer";

    // Constructor to inject JwtService and AuthenticationManager dependencies
    public LoginController(JwtService jwtService, AuthenticationManager authenticationManager,
            AppUserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    // Handle login request
    @PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody AccountCredentials credentials) {
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.username(),
                credentials.password()); // Obtain {username, password} from login page
        // Check if user exists in database
        // This authentication process is performed by loadUserByUsername() in
        // UserDetailsServiceImpl
        // If user does not exist in database, a "Bad credentials" exception will be
        // thrown
        // and will not proceed to the next line
        Authentication auth = authenticationManager.authenticate(creds);
        String jwts = jwtService.getToken(auth.getName(), auth.getAuthorities());

        // Fetch user details from the database
        AppUser user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        // Build response with the user ID only
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer" + jwts)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                .body(Map.of("userId", user.getId(), "role", user.getRole()));
    }

    // Handle refresh token request
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer")) {
            Authentication auth = jwtService.getAuthentication(request);
            if (auth != null) {
                String newToken = jwtService.getToken(auth.getName(), auth.getAuthorities());
                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer" + newToken)
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
                        .build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Handle logout request
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
