package com.example.musicapi.dto;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
