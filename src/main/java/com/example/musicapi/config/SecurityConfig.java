package com.example.musicapi.config;
import java.util.stream.Collectors;

import com.example.musicapi.config.properties.CorsProperties;
import com.example.musicapi.config.properties.JwtProperties;
import com.example.musicapi.config.properties.RateLimitProperties;
import com.example.musicapi.security.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            .csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/api/v1/docs/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
            .and()
            .httpBasic().disable()
            .formLogin().disable();

        http.addFilterBefore(domainAllowlistFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(rateLimitFilter, JwtAuthFilter.class);

        return http.build();
    }
}
