package com.example.musicapi.service;
import java.util.stream.Collectors;

import com.example.musicapi.dto.AlbumCreatedEvent;
import com.example.musicapi.dto.AlbumResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AlbumNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public AlbumNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyAlbumCreated(AlbumResponse albumResponse, Instant createdAt) {
        AlbumCreatedEvent event = new AlbumCreatedEvent(
                albumResponse.getId(),
                albumResponse.getTitle(),
                albumResponse.getArtists(),
                createdAt
        );

        // broadcast
        messagingTemplate.convertAndSend("/topic/albums", event);
    }
}
