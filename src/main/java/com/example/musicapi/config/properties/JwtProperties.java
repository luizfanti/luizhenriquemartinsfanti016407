package com.example.musicapi.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private String issuer = "musicapi";
    private String secret = "CHANGE_ME_SUPER_SECRET_64_CHARS_MIN";
    private int accessTokenExpMinutes = 5;
    private int refreshTokenExpDays = 7;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getAccessTokenExpMinutes() {
        return accessTokenExpMinutes;
    }

    public void setAccessTokenExpMinutes(int accessTokenExpMinutes) {
        this.accessTokenExpMinutes = accessTokenExpMinutes;
    }

    public int getRefreshTokenExpDays() {
        return refreshTokenExpDays;
    }

    public void setRefreshTokenExpDays(int refreshTokenExpDays) {
        this.refreshTokenExpDays = refreshTokenExpDays;
    }
}
