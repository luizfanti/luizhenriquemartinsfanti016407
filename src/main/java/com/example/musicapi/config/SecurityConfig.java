package com.example.musicapi.config;

import com.example.musicapi.config.properties.CorsProperties;
import com.example.musicapi.config.properties.JwtProperties;
import com.example.musicapi.security.DomainAllowlistFilter;
import com.example.musicapi.security.JwtAuthFilter;
import com.example.musicapi.security.JwtService;
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
@EnableConfigurationProperties({CorsProperties.class, JwtProperties.class})
public class SecurityConfig {

    @Bean
    public JwtService jwtService(JwtProperties jwtProperties) {
        return new JwtService(jwtProperties);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsProperties corsProperties,
            JwtService jwtService
    ) throws Exception {

        DomainAllowlistFilter domainAllowlistFilter = new DomainAllowlistFilter(corsProperties);
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtService);

        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                .requestMatchers("/api/v1/docs/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth aberto
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Todo resto versionado exige JWT
                .requestMatchers("/api/v1/**").authenticated()

                // Demais rotas (se existirem) podem ficar abertas ou fechadas
                .anyRequest().permitAll()
            )

            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable());

        http.addFilterBefore(domainAllowlistFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
