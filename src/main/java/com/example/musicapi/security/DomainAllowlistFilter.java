package com.example.musicapi.security;

import com.example.musicapi.config.properties.CorsProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

/**
 * Requisito do edital:
 * - Bloquear acesso aos endpoints a partir de domínios fora do domínio do serviço.
 *
 * Estratégia (objetiva e documentável):
 * - Se houver header Origin: valida se o hostname do Origin termina com serviceDomain.
 * - Se não houver Origin (ex.: curl/postman): valida o Host.
 *
 * Observação:
 * - Em chamadas internas (ex.: healthcheck), normalmente Host bate com o domínio/localhost.
 */
public class DomainAllowlistFilter extends OncePerRequestFilter {

    private final String serviceDomain;

    public DomainAllowlistFilter(CorsProperties corsProperties) {
        this.serviceDomain = normalize(corsProperties.getServiceDomain());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Sempre permitir OPTIONS (preflight CORS); CORS será tratado pelo CorsConfigurationSource.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = request.getHeader(HttpHeaders.ORIGIN);
        String hostHeader = request.getHeader(HttpHeaders.HOST);

        boolean allowed = isAllowed(origin, hostHeader);

        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {"error":"forbidden","message":"Request blocked: origin/host not allowed for this service domain."}
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAllowed(String origin, String hostHeader) {
        if (!StringUtils.hasText(serviceDomain)) {
            // Se não configurado, não bloqueia (mas mantenha configurado em produção).
            return true;
        }

        // Preferência: Origin (navegador)
        if (StringUtils.hasText(origin)) {
            String originHost = extractHost(origin);
            return hostMatchesDomain(originHost, serviceDomain);
        }

        // Fallback: Host (curl/postman e chamadas server-to-server)
        if (StringUtils.hasText(hostHeader)) {
            String hostOnly = hostHeader.split(":")[0];
            return hostMatchesDomain(normalize(hostOnly), serviceDomain);
        }

        // Sem Origin e sem Host: bloqueia por segurança
        return false;
    }

    private String extractHost(String origin) {
        try {
            URI uri = URI.create(origin);
            return normalize(uri.getHost());
        } catch (Exception ignored) {
            return "";
        }
    }

    private boolean hostMatchesDomain(String host, String domain) {
        if (!StringUtils.hasText(host)) return false;

        // Ex.: domain=localhost -> aceita localhost e localhost:*
        // Ex.: domain=seplag.mt.gov.br -> aceita api.seplag.mt.gov.br, seplag.mt.gov.br
        if (host.equals(domain)) return true;
        return host.endsWith("." + domain);
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) return "";
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
