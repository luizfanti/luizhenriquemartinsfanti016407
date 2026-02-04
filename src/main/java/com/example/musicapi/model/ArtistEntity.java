package com.example.musicapi.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "artist")
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ArtistType type;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @ManyToMany(mappedBy = "artists")
    private Set<AlbumEntity> albums = new HashSet<>();

    public ArtistEntity() {}

    public ArtistEntity(String name, ArtistType type) {
        this.name = name;
        this.type = type;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public ArtistType getType() { return type; }
    public Instant getCreatedAt() { return createdAt; }
    public Set<AlbumEntity> getAlbums() { return albums; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(ArtistType type) { this.type = type; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setAlbums(Set<AlbumEntity> albums) { this.albums = albums; }
}