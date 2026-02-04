package com.example.musicapi.config;

import com.example.musicapi.config.properties.CorsProperties;
import com.example.musicapi.config.properties.JwtProperties;
import com.example.musicapi.config.properties.RateLimitProperties;
import com.example.musicapi.security.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({CorsProperties.class, JwtProperties.class, RateLimitProperties.class})
public class SecurityConfig {

    @Bean
    public JwtService jwtService(JwtProperties jwtProperties) {
        return new JwtService(jwtProperties);
    }

    @Bean
    public RateLimitService rateLimitService() {
        return new RateLimitService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsProperties corsProperties,
            JwtService jwtService,
            RateLimitProperties rateLimitProperties,
            RateLimitService rateLimitService
    ) throws Exception {

        DomainAllowlistFilter domainAllowlistFilter = new DomainAllowlistFilter(corsProperties);
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService);
        RateLimitFilter rateLimitFilter = new RateLimitFilter(rateLimitProperties, rateLimitService);

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                .requestMatchers("/api/v1/docs/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                .requestMatchers("/api/v1/auth/**").permitAll()

                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        // Domínio primeiro
        http.addFilterBefore(domainAllowlistFilter, UsernamePasswordAuthenticationFilter.class);
        // JWT antes do rate limit (para setar principal)
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Rate limit depois do JWT
        http.addFilterAfter(rateLimitFilter, JwtAuthFilter.class);

        return http.build();
    }
}
