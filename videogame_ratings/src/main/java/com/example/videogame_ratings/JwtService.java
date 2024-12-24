package com.example.videogame_ratings;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Component
public class JwtService {
    // Set expiration time to 24 hours
    static final long EXPIRATIONTIME = 86400000;
    static final String PREFIX = "Bearer";
    
    // Generate secret key
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Generate signed JWT token
    public String getToken(String username, Collection<? extends GrantedAuthority> roles) {
        String rolesString = roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", rolesString)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(key)
                .compact();
    }

    // Get a token from request Authorization header
    // Verify the token and get username
    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody();

            String user = claims.getSubject();
            String roles = claims.get("roles", String.class);
            List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
        }
        return null;
    }

}
