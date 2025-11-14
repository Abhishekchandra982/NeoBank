package com.example.neobank.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private static final long Expiration_Time = 86400000; // 1 day in millisecond

    @Value("${jwtSecret}")
    private String jwtSecret;

    // --------------------------
    // Generate SecretKey
    // --------------------------
    public SecretKey generateKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --------------------------
    // Generate JWT Token
    // --------------------------
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + Expiration_Time); // 1 day expiry

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(generateKey(), Jwts.SIG.HS256)
                .compact();
    }

    // --------------------------
    // Extract Claims
    // --------------------------
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // --------------------------
    // Extract Subject (username)
    // --------------------------
    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    // --------------------------
    // Extract Expiry Date
    // --------------------------
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    // --------------------------
    // Check Validity
    // --------------------------
    public boolean isTokenValid(String token) {
        return new Date().before(extractExpiration(token));
    }
}
