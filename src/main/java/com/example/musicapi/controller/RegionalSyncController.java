package com.example.musicapi.controller;
import java.util.stream.Collectors;

import com.example.musicapi.dto.RegionalSyncResult;
import com.example.musicapi.service.RegionalSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/regionals")
public class RegionalSyncController {

    private final RegionalSyncService syncService;

    public RegionalSyncController(RegionalSyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Endpoint manual para executar a sincronização.
     * Protegido por JWT por estar em /api/v1/**.
     */
    @PostMapping("/sync")
    public ResponseEntity<RegionalSyncResult> sync() {
        return ResponseEntity.ok(syncService.sync());
    }
}
