package com.example.musicapi.dto;

import java.time.Instant;

public class AlbumImageResponse {

    private Long id;
    private String url;
    private String contentType;
    private Long size;
    private Instant createdAt;

    public AlbumImageResponse() {}

    public AlbumImageResponse(Long id, String url, String contentType, Long size, Instant createdAt) {
        this.id = id;
        this.url = url;
        this.contentType = contentType;
        this.size = size;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUrl() { return url; }
    public String getContentType() { return contentType; }
    public Long getSize() { return size; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUrl(String url) { this.url = url; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setSize(Long size) { this.size = size; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

