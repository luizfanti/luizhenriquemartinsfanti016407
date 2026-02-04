package com.example.musicapi.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.ratelimit")
public class RateLimitProperties {

    /**
     * Requisito do edital: 10 req/min por usuário autenticado.
     */
    private int requestsPerMinute = 10;

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }
}

