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

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlbumImageService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumNotificationService albumNotificationService;

    public AlbumImageService(
            AlbumRepository albumRepository,
            ArtistRepository artistRepository,
            AlbumNotificationService albumNotificationService
    ) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.albumNotificationService = albumNotificationService;
    }

    public AlbumResponse create(AlbumCreateRequest req) {
        Set<ArtistEntity> artists = loadArtists(req.getArtistIds());

        AlbumEntity entity = new AlbumEntity(req.getTitle());
        entity.setArtists(artists);

        AlbumEntity saved = albumRepository.save(entity);
        AlbumResponse response = toResponse(saved);

        // Notificar WebSocket (novo álbum cadastrado)
        // createdAt vem da entidade (instant), mas garantimos fallback
        Instant createdAt = saved.getCreatedAt() != null ? saved.getCreatedAt() : Instant.now();
        albumNotificationService.notifyAlbumCreated(response, createdAt);

        return response;
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

        return artistIds.stream()
                .map(id -> artistRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Artist not found: " + id)))
                .collect(Collectors.toSet());
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

    public @Nullable Object upload(Long albumId, List<MultipartFile> files) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    public @Nullable Object list(Long albumId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }
}
