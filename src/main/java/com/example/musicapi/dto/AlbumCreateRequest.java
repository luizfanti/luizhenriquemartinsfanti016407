package com.example.musicapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class AlbumCreateRequest {

    @NotBlank
    private String title;

    @NotEmpty
    private Set<Long> artistIds;

    public AlbumCreateRequest() {}

    public String getTitle() { return title; }
    public Set<Long> getArtistIds() { return artistIds; }

    public void setTitle(String title) { this.title = title; }
    public void setArtistIds(Set<Long> artistIds) { this.artistIds = artistIds; }
}
