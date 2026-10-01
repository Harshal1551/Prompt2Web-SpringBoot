package com.prompt2web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;

    private static final long EXPIRATION_TIME =
            1000L * 60 * 60 * 24;

    public JwtService(
            @Value("${jwt.secret}") String secretKey
    ) {
        this.key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(
            String userId,
            String email
    ) {
        Date now = new Date();

        Date expiration =
                new Date(
                        now.getTime() + EXPIRATION_TIME
                );

        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String extractUserId(
            String token
    ) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(
            String token
    ) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}