package com.example.musicapi.config;

import com.example.musicapi.config.properties.CorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsProperties props) {
        CorsConfiguration cfg = new CorsConfiguration();

        // Allowlist restritiva (configurável por env)
        cfg.setAllowedOrigins(normalizeOrigins(props.getAllowlist().getOrigins()));

        // Métodos exigidos pelo edital (GET/POST/PUT) + OPTIONS (preflight)
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));

        // Headers comuns (inclui Authorization para JWT)
        cfg.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        // Headers expostos (opcional)
        cfg.setExposedHeaders(List.of("Location"));

        // JWT via header, sem cookie -> pode manter false
        cfg.setAllowCredentials(false);

        // Cache de preflight
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    private List<String> normalizeOrigins(List<String> origins) {
        if (origins == null) return List.of();
        return origins.stream()
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}
