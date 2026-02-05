package com.example.musicapi.service;
import java.util.stream.Collectors;

import com.example.musicapi.config.properties.JwtProperties;
import com.example.musicapi.dto.AuthResponse;
import com.example.musicapi.model.RefreshTokenEntity;
import com.example.musicapi.repository.RefreshTokenRepository;
import com.example.musicapi.security.JwtService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class AuthService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    public AuthService(JwtProperties jwtProperties,
                       RefreshTokenRepository refreshTokenRepository,
                       JwtService jwtService) {
        this.jwtProperties = jwtProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse login(String username, String password) {
        // Requisito: usuário fixo para testes (seed/README). Por enquanto hardcoded:
        if (!"admin".equals(username) || !"admin".equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = generateRefreshToken();

        Instant expiresAt = Instant.now().plusSeconds((long) jwtProperties.getRefreshTokenExpDays() * 24 * 3600);

        refreshTokenRepository.save(new RefreshTokenEntity(username, refreshToken, expiresAt));

        long expSeconds = (long) jwtProperties.getAccessTokenExpMinutes() * 60;

        return new AuthResponse(accessToken, refreshToken, expSeconds);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshTokenEntity entity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (entity.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new IllegalArgumentException("Refresh token expired");
        }

        String accessToken = jwtService.generateAccessToken(entity.getUsername());

        long expSeconds = (long) jwtProperties.getAccessTokenExpMinutes() * 60;

        return new AuthResponse(accessToken, refreshToken, expSeconds);
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

