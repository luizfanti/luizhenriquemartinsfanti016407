package com.example.musicapi.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.regionals")
public class IntegrationProperties {

    private String baseUrl = "https://integrador-argus-api.geia.vip";
    private String path = "/v1/regionais";

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

