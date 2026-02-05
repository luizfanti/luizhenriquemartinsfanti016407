package com.example.musicapi.repository;
import java.util.stream.Collectors;

import com.example.musicapi.model.AlbumEntity;
import com.example.musicapi.model.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {

    @Query("select distinct a from AlbumEntity a join a.artists ar where ar.type = :type")
    Page<AlbumEntity> findByArtistType(ArtistType type, Pageable pageable);
}

