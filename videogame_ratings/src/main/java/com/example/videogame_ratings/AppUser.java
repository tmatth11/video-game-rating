package com.example.videogame_ratings;

import jakarta.persistence.*;

@Entity
public class AppUser {
    // ID field of AppUser entity
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    // Username field of AppUser entity
    @Column(nullable = false, unique = true)
    private String username;

    // Password field of AppUser entity
    @Column(nullable = false)
    private String password;

    // Role field of AppUser entity
    @Column(nullable = false)
    private String role;

    // Default constructor
    public AppUser() {}

    // Constructor with initialized fields
    public AppUser(String username, String password, String role) {
        super();
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and setters

    // Getter and setter of ID field
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and setter of username field
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and setter of password field
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and setter of role field
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
