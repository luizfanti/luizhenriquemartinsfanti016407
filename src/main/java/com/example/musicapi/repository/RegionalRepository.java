package com.example.musicapi.repository;
import java.util.stream.Collectors;

import com.example.musicapi.model.RegionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionalRepository extends JpaRepository<RegionalEntity, Long> {

    List<RegionalEntity> findByAtivoTrue();

}