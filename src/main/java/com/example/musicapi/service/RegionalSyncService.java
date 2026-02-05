package com.example.musicapi.service;
import java.util.stream.Collectors;

import com.example.musicapi.dto.ExternalRegionalDto;
import com.example.musicapi.dto.RegionalSyncResult;
import com.example.musicapi.model.RegionalEntity;
import com.example.musicapi.repository.RegionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RegionalSyncService {

    private final RegionalIntegrationClient client;
    private final RegionalRepository regionalRepository;

    public RegionalSyncService(RegionalIntegrationClient client, RegionalRepository regionalRepository) {
        this.client = client;
        this.regionalRepository = regionalRepository;
    }

    /**
     * Regras:
     * a) Nova no endpoint -> inserir (ativo=true)
     * b) Ausente no endpoint -> inativar (ativo=false)
     * c) Alterou atributo -> inativar antigo e criar novo
     */
    @Transactional
    public RegionalSyncResult sync() {
        List<ExternalRegionalDto> external = Optional.ofNullable(client.fetchRegionals())
                .orElseGet(List::of);

        int inserted = 0;
        int inactivated = 0;
        int recreated = 0;

        Map<Integer, String> externalMap = external.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(
                        ExternalRegionalDto::getId,
                        e -> safeNome(e.getNome()),
                        (a, b) -> a
                ));

        List<RegionalEntity> localActive = regionalRepository.findByAtivoTrue();
        Map<Integer, RegionalEntity> localActiveByExternalId = localActive.stream()
                .collect(Collectors.toMap(RegionalEntity::getExternalId, r -> r, (a, b) -> a));

        for (Map.Entry<Integer, String> entry : externalMap.entrySet()) {
            Integer id = entry.getKey();
            String nome = entry.getValue();

            RegionalEntity local = localActiveByExternalId.get(id);
            if (local == null) {
                regionalRepository.save(new RegionalEntity(id, nome, true));
                inserted++;
                continue;
            }

            if (!Objects.equals(local.getNome(), nome)) {
                local.setAtivo(false);
                regionalRepository.save(local);
                inactivated++;

                regionalRepository.save(new RegionalEntity(id, nome, true));
                recreated++;
            }
        }

        Set<Integer> externalIds = externalMap.keySet();
        for (RegionalEntity local : localActive) {
            if (!externalIds.contains(local.getExternalId())) {
                local.setAtivo(false);
                regionalRepository.save(local);
                inactivated++;
            }
        }

        return new RegionalSyncResult(inserted, inactivated, recreated, external.size());
    }

    private String safeNome(String nome) {
        if (nome == null) return "";
        return nome.trim();
    }
}