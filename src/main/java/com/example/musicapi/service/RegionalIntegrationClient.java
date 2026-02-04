package com.example.musicapi.service;

import com.example.musicapi.config.properties.IntegrationProperties;
import com.example.musicapi.dto.ExternalRegionalDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
public class RegionalIntegrationClient {

    private final WebClient webClient;
    private final IntegrationProperties props;

    public RegionalIntegrationClient(WebClient webClient, IntegrationProperties props) {
        this.webClient = webClient;
        this.props = props;
    }

    public List<ExternalRegionalDto> fetchRegionals() {
        return webClient.get()
                .uri(props.getBaseUrl() + props.getPath())
                .retrieve()
                .bodyToFlux(ExternalRegionalDto.class)
                .collectList()
                .timeout(Duration.ofSeconds(15))
                .block();
    }
}