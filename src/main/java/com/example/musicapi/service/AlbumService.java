package com.example.musicapi.service;

import com.example.musicapi.dto.AlbumCreateRequest;
import com.example.musicapi.dto.AlbumResponse;
import com.example.musicapi.dto.ArtistResponse;
import com.example.musicapi.exception.NotFoundException;
import com.example.musicapi.model.AlbumEntity;
import com.example.musicapi.model.ArtistEntity;
import com.example.musicapi.model.ArtistType;
import com.example.musicapi.repository.AlbumRepository;
import com.example.musicapi.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
    }

    public AlbumResponse create(AlbumCreateRequest req) {
        Set<ArtistEntity> artists = loadArtists(req.getArtistIds());
        AlbumEntity entity = new AlbumEntity(req.getTitle());
        entity.setArtists(artists);

        AlbumEntity saved = albumRepository.save(entity);
        return toResponse(saved);
    }

    public AlbumResponse update(Long id, AlbumCreateRequest req) {
        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found: " + id));

        entity.setTitle(req.getTitle());
        entity.setArtists(loadArtists(req.getArtistIds()));

        AlbumEntity saved = albumRepository.save(entity);
        return toResponse(saved);
    }

    public AlbumResponse getById(Long id) {
        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found: " + id));
        return toResponse(entity);
    }

    public Page<AlbumResponse> listPaged(Integer page, Integer size) {
        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 10 : Math.min(Math.max(size, 1), 50);

        PageRequest pr = PageRequest.of(p, s, Sort.by(Sort.Direction.ASC, "title"));
        return albumRepository.findAll(pr).map(this::toResponse);
    }

    public Page<AlbumResponse> listByArtistType(String type, Integer page, Integer size) {
        ArtistType artistType = parseArtistType(type);

        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 10 : Math.min(Math.max(size, 1), 50);

        PageRequest pr = PageRequest.of(p, s, Sort.by(Sort.Direction.ASC, "title"));
        return albumRepository.findByArtistType(artistType, pr).map(this::toResponse);
    }

    private Set<ArtistEntity> loadArtists(Set<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            throw new IllegalArgumentException("artistIds is required");
        }

        Set<ArtistEntity> artists = artistIds.stream()
                .map(id -> artistRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Artist not found: " + id)))
                .collect(Collectors.toSet());

        return artists;
    }

    private ArtistType parseArtistType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("type is required: singer|band");
        }
        if ("singer".equalsIgnoreCase(type)) return ArtistType.SINGER;
        if ("band".equalsIgnoreCase(type)) return ArtistType.BAND;
        throw new IllegalArgumentException("invalid type: " + type + " (use singer|band)");
    }

    private AlbumResponse toResponse(AlbumEntity e) {
        Set<ArtistResponse> artists = e.getArtists().stream()
                .map(ar -> new ArtistResponse(ar.getId(), ar.getName(), ar.getType()))
                .collect(Collectors.toSet());

        return new AlbumResponse(e.getId(), e.getTitle(), artists);
    }
}

