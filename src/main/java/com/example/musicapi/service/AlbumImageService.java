package com.example.musicapi.service;
import java.util.stream.Collectors;

import com.example.musicapi.config.properties.MinioProperties;
import com.example.musicapi.dto.AlbumCreateRequest;
import com.example.musicapi.dto.AlbumImageResponse;
import com.example.musicapi.dto.AlbumResponse;
import com.example.musicapi.dto.ArtistResponse;
import com.example.musicapi.exception.NotFoundException;
import com.example.musicapi.model.AlbumEntity;
import com.example.musicapi.model.AlbumImageEntity;
import com.example.musicapi.model.ArtistEntity;
import com.example.musicapi.model.ArtistType;
import com.example.musicapi.repository.AlbumImageRepository;
import com.example.musicapi.repository.AlbumRepository;
import com.example.musicapi.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class AlbumImageService {

    private final AlbumRepository albumRepository;
    private final AlbumImageRepository albumImageRepository;
    private final ArtistRepository artistRepository;
    private final AlbumNotificationService albumNotificationService;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final MinioProperties minioProperties;

    public AlbumImageService(
            AlbumRepository albumRepository,
            AlbumImageRepository albumImageRepository,
            ArtistRepository artistRepository,
            AlbumNotificationService albumNotificationService,
            S3Client s3Client,
            S3Presigner s3Presigner,
            MinioProperties minioProperties
    ) {
        this.albumRepository = albumRepository;
        this.albumImageRepository = albumImageRepository;
        this.artistRepository = artistRepository;
        this.albumNotificationService = albumNotificationService;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.minioProperties = minioProperties;
    }

    // (mantido caso você use em algum lugar; o controller de álbum usa AlbumService)
    public AlbumResponse create(AlbumCreateRequest req) {
        Set<ArtistEntity> artists = loadArtists(req.getArtistIds());

        AlbumEntity entity = new AlbumEntity(req.getTitle());
        entity.setArtists(artists);

        AlbumEntity saved = albumRepository.save(entity);
        AlbumResponse response = toResponse(saved);

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

    public List<AlbumImageResponse> upload(Long albumId, List<MultipartFile> files) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found: " + albumId));

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("files is required");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String objectKey = buildObjectKey(albumId, file.getOriginalFilename());
            putObject(objectKey, file);

            AlbumImageEntity meta = new AlbumImageEntity(
                    albumId,
                    objectKey,
                    file.getContentType(),
                    file.getSize()
            );
            albumImageRepository.save(meta);
        }

        return list(albumId);
    }

    public List<AlbumImageResponse> list(Long albumId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found: " + albumId));

        return albumImageRepository.findByAlbumIdOrderByCreatedAtDesc(albumId).stream()
                .map(img -> new AlbumImageResponse(
                        img.getId(),
                        presignedGetUrl(img.getObjectKey()),
                        img.getContentType(),
                        img.getSize(),
                        img.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    private String buildObjectKey(Long albumId, String originalFilename) {
        String clean = (originalFilename == null || originalFilename.isBlank())
                ? "file"
                : originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "albums/" + albumId + "/" + UUID.randomUUID() + "_" + clean;
    }

    private void putObject(String objectKey, MultipartFile file) {
        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(minioProperties.getBucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    private String presignedGetUrl(String objectKey) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(minioProperties.getBucket())
                .key(objectKey)
                .build();

        int expMin = minioProperties.getPresignedUrlExpMinutes() == null
                ? 30
                : Math.max(1, minioProperties.getPresignedUrlExpMinutes());

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expMin))
                .getObjectRequest(getReq)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignReq);
        return presigned.url().toString();
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
}