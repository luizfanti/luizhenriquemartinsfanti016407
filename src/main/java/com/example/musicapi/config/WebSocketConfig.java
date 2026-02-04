package com.example.musicapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Cliente conecta em:
     *  - ws://host:8080/ws  (ou SockJS)
     *
     * Cliente assina:
     *  - /topic/albums
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");          // broker em memória (suficiente pro desafio)
        registry.setApplicationDestinationPrefixes("/app"); // reservado para mensagens do cliente -> server (não usado agora)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // restritivo por CORS (usa o mesmo allowlist do Spring; em dev pode ficar "*")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}