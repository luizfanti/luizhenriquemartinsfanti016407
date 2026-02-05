package com.example.musicapi.controller;
import java.util.stream.Collectors;

import com.example.musicapi.dto.ArtistCreateRequest;
import com.example.musicapi.dto.ArtistResponse;
import com.example.musicapi.service.ArtistService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    public ResponseEntity<ArtistResponse> create(@Valid @RequestBody ArtistCreateRequest req) {
        ArtistResponse created = artistService.create(req);
        return ResponseEntity.created(URI.create("/api/v1/artists/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistResponse> update(@PathVariable Long id, @Valid @RequestBody ArtistCreateRequest req) {
        return ResponseEntity.ok(artistService.update(id, req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getById(id));
    }

    /**
     * Consulta por nome com ordenação alfabética asc/desc.
     * Ex: /api/v1/artists?name=mi&order=asc
     */
    @GetMapping
    public ResponseEntity<List<ArtistResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "asc") String order
    ) {
        return ResponseEntity.ok(artistService.searchByName(name, order));
    }
}

