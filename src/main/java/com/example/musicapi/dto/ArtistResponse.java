package com.example.musicapi.dto;

import com.example.musicapi.model.ArtistType;

public class ArtistResponse {

    private Long id;
    private String name;
    private ArtistType type;

    public ArtistResponse() {}

    public ArtistResponse(Long id, String name, ArtistType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public ArtistType getType() { return type; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(ArtistType type) { this.type = type; }
}

