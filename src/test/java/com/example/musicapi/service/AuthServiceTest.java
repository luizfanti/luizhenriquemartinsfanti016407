package com.example.musicapi.service;

import com.example.musicapi.config.properties.JwtProperties;
import com.example.musicapi.dto.AuthResponse;
import com.example.musicapi.model.RefreshTokenEntity;
import com.example.musicapi.repository.RefreshTokenRepository;
import com.example.musicapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtProperties jwtProperties;
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setup() {
        jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("musicapi-test");
        jwtProperties.setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        jwtProperties.setAccessTokenExpMinutes(5);
        jwtProperties.setRefreshTokenExpDays(7);

        jwtService = new JwtService(jwtProperties);

        authService = new AuthService(jwtProperties, refreshTokenRepository, jwtService);
    }

    @Test
    void login_shouldReturnAccessAndRefresh_andPersistRefreshToken() {
        ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse resp = authService.login("admin", "admin");

        assertNotNull(resp);
        assertNotNull(resp.getAccessToken());
        assertNotNull(resp.getRefreshToken());
        assertEquals(300, resp.getExpiresInSeconds());
        assertEquals("Bearer", resp.getTokenType());

        verify(refreshTokenRepository, times(1)).save(captor.capture());
        RefreshTokenEntity saved = captor.getValue();

        assertEquals("admin", saved.getUsername());
        assertEquals(resp.getRefreshToken(), saved.getToken());
        assertTrue(saved.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void login_invalidCredentials_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> authService.login("admin", "wrong"));
        assertThrows(IllegalArgumentException.class, () -> authService.login("user", "admin"));
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void refresh_validToken_shouldReturnNewAccessToken() {
        String refresh = "refresh-token-value";
        RefreshTokenEntity entity = new RefreshTokenEntity("admin", refresh, Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken(refresh)).thenReturn(Optional.of(entity));

        AuthResponse resp = authService.refresh(refresh);

        assertNotNull(resp.getAccessToken());
        assertEquals(refresh, resp.getRefreshToken());
        assertEquals(300, resp.getExpiresInSeconds());
        verify(refreshTokenRepository, times(1)).findByToken(refresh);
    }

    @Test
    void refresh_expiredToken_shouldDeleteAndThrow() {
        String refresh = "expired-refresh";
        RefreshTokenEntity entity = new RefreshTokenEntity("admin", refresh, Instant.now().minusSeconds(5));
        when(refreshTokenRepository.findByToken(refresh)).thenReturn(Optional.of(entity));

        assertThrows(IllegalArgumentException.class, () -> authService.refresh(refresh));

        verify(refreshTokenRepository).deleteByToken(refresh);
    }

    @Test
    void refresh_invalidToken_shouldThrow() {
        when(refreshTokenRepository.findByToken("nope")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authService.refresh("nope"));
    }
}