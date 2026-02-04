package com.example.musicapi.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthLoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public AuthLoginRequest() {}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}