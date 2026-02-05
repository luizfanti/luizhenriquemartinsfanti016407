package com.example.musicapi.dto;
import java.util.stream.Collectors;

import java.util.Set;

public class AlbumResponse {

    private Long id;
    private String title;
    private Set<ArtistResponse> artists;

    public AlbumResponse() {}

    public AlbumResponse(Long id, String title, Set<ArtistResponse> artists) {
        this.id = id;
        this.title = title;
        this.artists = artists;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Set<ArtistResponse> getArtists() { return artists; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtists(Set<ArtistResponse> artists) { this.artists = artists; }
}
