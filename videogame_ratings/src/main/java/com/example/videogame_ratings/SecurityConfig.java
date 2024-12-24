package com.example.videogame_ratings;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationFilter authenticationFilter;
    private final AuthEntryPoint exceptionHandler;

    // Constructor to inject dependencies
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthenticationFilter authenticationFilter,
            AuthEntryPoint exceptionHandler) {
        this.userDetailsService = userDetailsService;
        this.authenticationFilter = authenticationFilter;
        this.exceptionHandler = exceptionHandler;
    }

    // Configure global authentication manager
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Telling Spring Security that we will check login user against database
        // how to authenticate login user is defined in loadUserByUsername() of
        // UserDetailsServiceImpl
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    // Configure security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Allow anyone to access GET endpoints for games and reviews
                        .requestMatchers(HttpMethod.GET, "/api/game/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/review/**").permitAll()
                        // Allow anyone to access login, logout, registration, and token refresh
                        // endpoints
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/refresh").permitAll()
                        // Game endpoints - ADMIN only for POST, PUT, DELETE
                        .requestMatchers(HttpMethod.POST, "/api/game").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/game/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/game/**").hasRole("ADMIN")
                        // Review endpoints - USER role for POST, PUT, DELETE
                        .requestMatchers(HttpMethod.POST, "/api/review").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/review/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/review/**").hasRole("USER")
                        // Any other requests require authentication
                        .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(exceptionHandler));
        return http.build();
    }

    // Configure password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Configure CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(false);
        config.applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
