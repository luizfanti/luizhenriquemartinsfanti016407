package com.example.musicapi.controller;

import com.example.musicapi.dto.AlbumImageResponse;
import com.example.musicapi.service.AlbumImageService;

import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums/{albumId}/images")
public class AlbumImageController {

    private final AlbumImageService albumImageService;

    public AlbumImageController(AlbumImageService albumImageService) {
        this.albumImageService = albumImageService;
    }

    /**
     * Upload de uma ou mais imagens de capa do álbum.
     * Form-data: files (multi)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<@Nullable Object> upload(
            @PathVariable Long albumId,
            @RequestPart("files") List<MultipartFile> files
    ) {
        return ResponseEntity.ok(albumImageService.upload(albumId, files));
    }

    /**
     * Listar imagens do álbum com links pré-assinados (30 min).
     */
    @GetMapping
    public ResponseEntity<List<AlbumImageResponse>> list(@PathVariable Long albumId) {
        return ResponseEntity.ok((List<AlbumImageResponse>) albumImageService.list(albumId));
    }
}
