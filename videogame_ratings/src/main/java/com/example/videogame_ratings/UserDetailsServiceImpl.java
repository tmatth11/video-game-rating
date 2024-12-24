package com.example.videogame_ratings;

import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository repository;

    // Constructor to inject the AppUserRepository dependency
    public UserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }

    // Load user by username from the database
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if username exists in database
        Optional<AppUser> user = repository.findByUsername(username);
        User.UserBuilder builder = null;
        // If user exists in database, build UserDetails object
        if (user.isPresent()) {
            AppUser currentUser = user.get();
            builder = org.springframework.security.core.userdetails.User.withUsername(username);
            builder.password(currentUser.getPassword());
            builder.roles(currentUser.getRole());
        } else {
            // If user does not exist in database, throw exception
            throw new UsernameNotFoundException("User not found.");
        }

        // Return UserDetails object
        return builder.build();
    }
}
