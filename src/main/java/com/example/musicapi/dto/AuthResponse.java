package com.example.musicapi.dto;
import java.util.stream.Collectors;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private long expiresInSeconds;
    private String tokenType = "Bearer";

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpiresInSeconds(long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
