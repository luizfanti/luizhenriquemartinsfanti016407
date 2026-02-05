package com.example.musicapi.controller;
import java.util.stream.Collectors;

import com.example.musicapi.dto.AlbumCreateRequest;
import com.example.musicapi.dto.AlbumResponse;
import com.example.musicapi.service.AlbumService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody AlbumCreateRequest req) {
        AlbumResponse created = albumService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/albums/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> update(@PathVariable Long id, @Valid @RequestBody AlbumCreateRequest req) {
        return ResponseEntity.ok(albumService.update(id, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(albumService.getById(id));
    }

    /**
     * Paginação de álbuns:
     * /api/v1/albums?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> listPaged(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(albumService.listPaged(page, size));
    }

    /**
     * Consulta parametrizada:
     * /api/v1/albums/by-artist-type?type=singer&page=0&size=10
     * /api/v1/albums/by-artist-type?type=band&page=0&size=10
     */
    @GetMapping("/by-artist-type")
    public ResponseEntity<Page<AlbumResponse>> listByArtistType(
            @RequestParam String type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return ResponseEntity.ok(albumService.listByArtistType(type, page, size));
    }
}

