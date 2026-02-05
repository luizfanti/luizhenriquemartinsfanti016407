package com.example.musicapi.config.properties;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private final Allowlist allowlist = new Allowlist();

    /**
     * Domínio do serviço (ex.: "meuservico.gov.br" ou "localhost").
     * Usado pelo filtro DomainAllowlistFilter para bloquear origens/hosts fora do domínio.
     */
    private String serviceDomain = "localhost";

    public Allowlist getAllowlist() {
        return allowlist;
    }

    public String getServiceDomain() {
        return serviceDomain;
    }

    public void setServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
    }

    public static class Allowlist {
        /**
         * Lista de origins permitidas para CORS.
         * Vem do application.yml/env: cors.allowlist.origins (string com vírgulas).
         */
        private List<String> origins = new ArrayList<>();

        public List<String> getOrigins() {
            return origins;
        }

        public void setOrigins(List<String> origins) {
            this.origins = origins;
        }
    }
}