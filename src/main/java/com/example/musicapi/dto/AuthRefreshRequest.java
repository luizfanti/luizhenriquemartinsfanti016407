package com.example.musicapi.dto;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

public class AuthRefreshRequest {

    @NotBlank
    private String refreshToken;

    public AuthRefreshRequest() {}

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
