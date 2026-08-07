package com.example.codevault_backend;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// @Component registers JwtUtil as a Spring bean so we can @Autowire it anywhere
@Component
public class JwtUtil {

    // Secret Key used to stamp (sign) and verify our JWT wristbands (must be >= 256
    // bits)
    private static final String SECRET_KEY_STRING = "CodeVaultSuperSecretKeyForJWTAuthentication2026!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // Token Validity: 10 Hours (in milliseconds)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // 1. Generate a new JWT Wristband for a successfully logged-in user
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Read the username from an incoming JWT token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 3. Check if the JWT token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 4. Validate if the JWT token belongs to the right user and is still valid
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Helper method to parse claims from token signature
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
