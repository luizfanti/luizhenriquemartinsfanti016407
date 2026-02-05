package com.example.musicapi.repository;
import java.util.stream.Collectors;

import com.example.musicapi.model.AlbumImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumImageRepository extends JpaRepository<AlbumImageEntity, Long> {
    List<AlbumImageEntity> findByAlbumIdOrderByCreatedAtDesc(Long albumId);
}

