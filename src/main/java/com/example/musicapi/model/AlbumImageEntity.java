package com.example.musicapi.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "album_image")
public class AlbumImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "album_id", nullable = false)
    private Long albumId;

    @Column(name = "object_key", nullable = false, length = 255)
    private String objectKey;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public AlbumImageEntity() {}

    public AlbumImageEntity(Long albumId, String objectKey, String contentType, Long size) {
        this.albumId = albumId;
        this.objectKey = objectKey;
        this.contentType = contentType;
        this.size = size;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getAlbumId() { return albumId; }
    public String getObjectKey() { return objectKey; }
    public String getContentType() { return contentType; }
    public Long getSize() { return size; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setAlbumId(Long albumId) { this.albumId = albumId; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setSize(Long size) { this.size = size; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
