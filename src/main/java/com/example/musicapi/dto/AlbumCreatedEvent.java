package com.example.musicapi.dto;
import java.util.stream.Collectors;

import java.time.Instant;
import java.util.Set;

public class AlbumCreatedEvent {

    private Long id;
    private String title;
    private Set<ArtistResponse> artists;
    private Instant createdAt;

    public AlbumCreatedEvent() {}

    public AlbumCreatedEvent(Long id, String title, Set<ArtistResponse> artists, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Set<ArtistResponse> getArtists() { return artists; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtists(Set<ArtistResponse> artists) { this.artists = artists; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
