package com.example.musicapi.security;

import com.example.musicapi.config.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class JwtService {

    private final JwtProperties props;

    public JwtService(JwtProperties props) {
        this.props = props;
    }

    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds((long) props.getAccessTokenExpMinutes() * 60);

        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("typ", "access")
                .signWith(Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public Instant getAccessTokenExpirationInstant() {
        return Instant.now().plusSeconds((long) props.getAccessTokenExpMinutes() * 60);
    }

    public String getUsernameIfValid(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}