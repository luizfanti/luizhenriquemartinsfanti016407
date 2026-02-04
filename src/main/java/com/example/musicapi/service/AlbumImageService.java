package com.example.musicapi.service;

import com.example.musicapi.config.properties.MinioProperties;
import com.example.musicapi.dto.AlbumImageResponse;
import com.example.musicapi.exception.NotFoundException;
import com.example.musicapi.model.AlbumEntity;
import com.example.musicapi.model.AlbumImageEntity;
import com.example.musicapi.repository.AlbumImageRepository;
import com.example.musicapi.repository.AlbumRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class AlbumImageService {

    private final MinioProperties props;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    private final AlbumRepository albumRepository;
    private final AlbumImageRepository albumImageRepository;

    private static final Duration PRESIGNED_EXPIRATION = Duration.ofMinutes(30);

    public AlbumImageService(
            MinioProperties props,
            S3Client s3Client,
            S3Presigner presigner,
            AlbumRepository albumRepository,
            AlbumImageRepository albumImageRepository
    ) {
        this.props = props;
        this.s3Client = s3Client;
        this.presigner = presigner;
        this.albumRepository = albumRepository;
        this.albumImageRepository = albumImageRepository;
    }

    public List<AlbumImageResponse> upload(Long albumId, List<MultipartFile> files) {
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found: " + albumId));

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("files is required");
        }

        return files.stream().map(file -> uploadOne(album.getId(), file)).toList();
    }

    public List<AlbumImageResponse> list(Long albumId) {
        // valida album existe
        albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found: " + albumId));

        return albumImageRepository.findByAlbumIdOrderByCreatedAtDesc(albumId).stream()
                .map(img -> new AlbumImageResponse(
                        img.getId(),
                        presignGetUrl(img.getObjectKey()),
                        img.getContentType(),
                        img.getSize(),
                        img.getCreatedAt()
                ))
                .toList();
    }

    private AlbumImageResponse uploadOne(Long albumId, MultipartFile file) {
        String objectKey = "albums/" + albumId + "/" + UUID.randomUUID() + "-" + safeName(file.getOriginalFilename());

        try {
            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(put, RequestBody.fromBytes(file.getBytes()));

            AlbumImageEntity saved = albumImageRepository.save(
                    new AlbumImageEntity(albumId, objectKey, file.getContentType(), file.getSize())
            );

            return new AlbumImageResponse(
                    saved.getId(),
                    presignGetUrl(objectKey),
                    saved.getContentType(),
                    saved.getSize(),
                    saved.getCreatedAt()
            );

        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read file: " + ex.getMessage());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to upload to storage: " + ex.getMessage());
        }
    }

    private String presignGetUrl(String objectKey) {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(props.getBucket())
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_EXPIRATION)
                .getObjectRequest(getReq)
                .build();

        PresignedGetObjectRequest presigned = presigner.presignGetObject(presignReq);
        return presigned.url().toString();
    }

    private String safeName(String name) {
        if (name == null || name.isBlank()) return "file";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
