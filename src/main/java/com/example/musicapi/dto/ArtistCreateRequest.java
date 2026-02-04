package com.example.musicapi.dto;

import com.example.musicapi.model.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArtistCreateRequest {

    @NotBlank
    private String name;

    @NotNull
    private ArtistType type;

    public ArtistCreateRequest() {}

    public String getName() { return name; }
    public ArtistType getType() { return type; }

    public void setName(String name) { this.name = name; }
    public void setType(ArtistType type) { this.type = type; }
}
