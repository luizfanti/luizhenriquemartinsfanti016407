package com.example.musicapi.config.properties;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint = "http://localhost:9000";

    /**
     * Endpoint público usado para gerar URLs pré-assinadas (host acessível pelo cliente).
     * Ex: http://localhost:9000 quando a API roda via docker-compose.
     */
    private String publicEndpoint = "http://localhost:9000";

    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucket = "musicapi";

    /** Expiração (em minutos) das URLs pré-assinadas. */
    private Integer presignedUrlExpMinutes = 30;

    public String getEndpoint() { return endpoint; }
    public String getPublicEndpoint() { return publicEndpoint; }
    public String getAccessKey() { return accessKey; }
    public String getSecretKey() { return secretKey; }
    public String getBucket() { return bucket; }
    public Integer getPresignedUrlExpMinutes() { return presignedUrlExpMinutes; }

    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public void setPublicEndpoint(String publicEndpoint) { this.publicEndpoint = publicEndpoint; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public void setBucket(String bucket) { this.bucket = bucket; }
    public void setPresignedUrlExpMinutes(Integer presignedUrlExpMinutes) {
        this.presignedUrlExpMinutes = presignedUrlExpMinutes;
    }
}