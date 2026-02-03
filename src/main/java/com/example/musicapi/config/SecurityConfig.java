package com.example.musicapi.config;

import com.example.musicapi.config.properties.CorsProperties;
import com.example.musicapi.security.DomainAllowlistFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Etapa atual:
 * - CORS restritivo (allowlist via env) ✅
 * - Bloqueio por domínio do serviço ✅
 *
 * Próxima etapa (JWT):
 * - Substituir permitAll() global por auth via JWT e refresh token.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsProperties corsProperties) throws Exception {

        // Filtro do edital: bloquear origens/hosts fora do domínio
        DomainAllowlistFilter domainAllowlistFilter = new DomainAllowlistFilter(corsProperties);

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Importante: neste momento, manter aberto para não travar o desenvolvimento.
            // Na Etapa JWT, vamos exigir autenticação nos endpoints /api/v1/** (exceto /auth e swagger/actuator).
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                .requestMatchers("/api/v1/docs/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().permitAll()
            )

            // Desliga auth default gerada automaticamente (não queremos Basic Login)
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        // Rodar o filtro antes de qualquer autenticação (quando JWT entrar)
        http.addFilterBefore(domainAllowlistFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}