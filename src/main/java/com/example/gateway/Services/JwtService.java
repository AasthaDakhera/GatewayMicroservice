package com.example.gateway.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token).getBody();
        String username = claims.get("username", String.class);
        return username;
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = parseToken(token).getBody();
        String useremail = claims.get("email", String.class);
        return useremail;
    }


    // Method to validate JWT token
    public boolean validateToken(String token) {
        try {
            parseToken(token);  // If parsing is successful, the token is valid
            return true;
        } catch (JwtException e) {
            // Handle any parsing exceptions such as ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, etc.
            return false;
        }
    }

    // Method to parse JWT token and get the Jws object
    private Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);

    }
}
