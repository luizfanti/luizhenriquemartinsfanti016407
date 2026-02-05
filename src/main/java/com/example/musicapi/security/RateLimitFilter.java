package com.example.musicapi.security;
import java.util.stream.Collectors;

import com.example.musicapi.config.properties.RateLimitProperties;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final RateLimitService rateLimitService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public RateLimitFilter(RateLimitProperties properties, RateLimitService rateLimitService) {
        this.properties = properties;
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Só aplicar para endpoints versionados
        if (!matcher.match("/api/v1/**", path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Não aplicar para auth (login/refresh)
        if (matcher.match("/api/v1/auth/**", path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Só limita se realmente estiver autenticado
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = String.valueOf(auth.getPrincipal());
        int limit = properties.getRequestsPerMinute();

        boolean allowed = rateLimitService.allow("user:" + username, limit);

        if (!allowed) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"too_many_requests\",\"message\":\"Rate limit exceeded. Max 10 requests per minute per user.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

