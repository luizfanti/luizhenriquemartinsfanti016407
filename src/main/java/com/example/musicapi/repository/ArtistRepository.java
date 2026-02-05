package com.example.musicapi.repository;
import java.util.stream.Collectors;

import com.example.musicapi.model.ArtistEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    List<ArtistEntity> findByNameContainingIgnoreCase(String name, Sort sort);
}
