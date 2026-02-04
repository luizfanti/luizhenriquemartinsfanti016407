package com.example.musicapi.service;

import com.example.musicapi.dto.ArtistCreateRequest;
import com.example.musicapi.dto.ArtistResponse;
import com.example.musicapi.exception.NotFoundException;
import com.example.musicapi.model.ArtistEntity;
import com.example.musicapi.repository.ArtistRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public ArtistResponse create(ArtistCreateRequest req) {
        ArtistEntity saved = artistRepository.save(new ArtistEntity(req.getName(), req.getType()));
        return toResponse(saved);
    }

    public ArtistResponse update(Long id, ArtistCreateRequest req) {
        ArtistEntity entity = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found: " + id));

        entity.setName(req.getName());
        entity.setType(req.getType());

        ArtistEntity saved = artistRepository.save(entity);
        return toResponse(saved);
    }

    public ArtistResponse getById(Long id) {
        ArtistEntity entity = artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found: " + id));
        return toResponse(entity);
    }

    public List<ArtistResponse> searchByName(String name, String order) {
        Sort sort = "desc".equalsIgnoreCase(order)
                ? Sort.by(Sort.Direction.DESC, "name")
                : Sort.by(Sort.Direction.ASC, "name");

        List<ArtistEntity> list = artistRepository.findByNameContainingIgnoreCase(name == null ? "" : name, sort);
        return list.stream().map(this::toResponse).toList();
    }

    private ArtistResponse toResponse(ArtistEntity e) {
        return new ArtistResponse(e.getId(), e.getName(), e.getType());
    }
}

