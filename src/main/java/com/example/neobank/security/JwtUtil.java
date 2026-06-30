package com.example.neobank.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET =
            "myVeryStrongSecretKeyForCollegeProject12345";

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 hours

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)
                .addClaims(Map.of(
                        "role", role
                ))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256) // 🔥 IMPORTANT
                .compact();
    }
}
