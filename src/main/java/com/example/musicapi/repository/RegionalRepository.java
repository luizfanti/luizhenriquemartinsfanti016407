package com.example.musicapi.repository;

import com.example.musicapi.model.RegionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionalRepository extends JpaRepository<RegionalEntity, Integer> {
    List<RegionalEntity> findByAtivoTrue();
}