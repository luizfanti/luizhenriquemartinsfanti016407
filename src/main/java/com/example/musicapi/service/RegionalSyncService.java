package com.example.musicapi.service;

import com.example.musicapi.dto.ExternalRegionalDto;
import com.example.musicapi.dto.RegionalSyncResult;
import com.example.musicapi.model.RegionalEntity;
import com.example.musicapi.repository.RegionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

        // Mapa do endpoint (id -> nome)
        Map<Integer, String> externalMap = external.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(
                        ExternalRegionalDto::getId,
                        e -> safeNome(e.getNome()),
                        (a, b) -> a
                ));

        // Estado atual local
        List<RegionalEntity> localAll = regionalRepository.findAll();
        Map<Integer, RegionalEntity> localById = localAll.stream()
                .collect(Collectors.toMap(RegionalEntity::getId, r -> r, (a, b) -> a));

        // a) Inserir novas
        for (Map.Entry<Integer, String> entry : externalMap.entrySet()) {
            Integer id = entry.getKey();
            String nome = entry.getValue();

            RegionalEntity local = localById.get(id);
            if (local == null) {
                regionalRepository.save(new RegionalEntity(id, nome, true));
                inserted++;
                continue;
            }

            // c) Se mudou nome e o registro atual está ativo, inativa e recria
            if (Boolean.TRUE.equals(local.getAtivo()) && !Objects.equals(local.getNome(), nome)) {
                local.setAtivo(false);
                regionalRepository.save(local);
                inactivated++;

                // recria novo com mesmo id + nome atualizado
                regionalRepository.save(new RegionalEntity(id, nome, true));
                recreated++;
            }

            // Se estava inativo e voltou no endpoint, reativar é uma escolha.
            // O edital não pediu reativação explícita, então mantemos simples:
            // se estiver inativo e existe no endpoint, criamos ativo novo (para “voltar”):
            if (Boolean.FALSE.equals(local.getAtivo())) {
                // cria um novo ativo com o mesmo id pode conflitar PK.
                // Como a tabela exige id único, o caminho correto é reativar ou recriar com novo id.
                // Como edital fixou id INTEGER, vamos REATIVAR no caso de voltar.
                local.setNome(nome);
                local.setAtivo(true);
                regionalRepository.save(local);
            }
        }

        // b) Inativar ausentes
        Set<Integer> externalIds = externalMap.keySet();
        for (RegionalEntity local : localAll) {
            if (Boolean.TRUE.equals(local.getAtivo()) && !externalIds.contains(local.getId())) {
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

