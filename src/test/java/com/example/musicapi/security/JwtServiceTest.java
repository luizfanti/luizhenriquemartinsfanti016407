package com.example.musicapi.security;

import com.example.musicapi.config.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setup() {
        JwtProperties props = new JwtProperties();
        props.setIssuer("musicapi-test");
        props.setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        props.setAccessTokenExpMinutes(5);

        jwtService = new JwtService(props);
    }

    @Test
    void generateAndValidate_shouldReturnUsername() {
        String token = jwtService.generateAccessToken("admin");
        assertNotNull(token);

        String subject = jwtService.getUsernameIfValid(token);
        assertEquals("admin", subject);
    }

    @Test
    void invalidToken_shouldThrow() {
        assertThrows(RuntimeException.class, () -> jwtService.getUsernameIfValid("invalid.token.here"));
    }
}