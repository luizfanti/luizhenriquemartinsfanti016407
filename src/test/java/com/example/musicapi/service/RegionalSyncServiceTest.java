package com.example.musicapi.service;

import com.example.musicapi.dto.ExternalRegionalDto;
import com.example.musicapi.dto.RegionalSyncResult;
import com.example.musicapi.model.RegionalEntity;
import com.example.musicapi.repository.RegionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalSyncServiceTest {

    @Mock
    private RegionalIntegrationClient client;

    @Mock
    private RegionalRepository regionalRepository;

    private RegionalSyncService service;

    @BeforeEach
    void setUp() {
        service = new RegionalSyncService(client, regionalRepository);
    }

    @Test
    void sync_ShouldInsertNewRegional() {
        // GIVEN
        ExternalRegionalDto ext = new ExternalRegionalDto();
        ext.setId(1);
        ext.setNome("Regional Norte");
        when(client.fetchRegionals()).thenReturn(List.of(ext));
        when(regionalRepository.findByAtivoTrue()).thenReturn(List.of());

        // WHEN
        RegionalSyncResult result = service.sync();

        // THEN
        assertEquals(1, result.getInserted());
        assertEquals(0, result.getInactivated());
        assertEquals(0, result.getRecreated());
        
        ArgumentCaptor<RegionalEntity> captor = ArgumentCaptor.forClass(RegionalEntity.class);
        verify(regionalRepository).save(captor.capture());
        RegionalEntity saved = captor.getValue();
        assertEquals(1, saved.getExternalId());
        assertEquals("Regional Norte", saved.getNome());
        assertTrue(saved.getAtivo());
    }

    @Test
    void sync_ShouldInactivateAbsentRegional() {
        // GIVEN
        when(client.fetchRegionals()).thenReturn(List.of());
        RegionalEntity local = new RegionalEntity(1, "Regional Norte", true);
        when(regionalRepository.findByAtivoTrue()).thenReturn(List.of(local));

        // WHEN
        RegionalSyncResult result = service.sync();

        // THEN
        assertEquals(0, result.getInserted());
        assertEquals(1, result.getInactivated());
        assertFalse(local.getAtivo());
        verify(regionalRepository).save(local);
    }

    @Test
    void sync_ShouldRecreateWhenAttributeChanged() {
        // GIVEN
        ExternalRegionalDto ext = new ExternalRegionalDto();
        ext.setId(1);
        ext.setNome("Regional Norte Alterada");
        when(client.fetchRegionals()).thenReturn(List.of(ext));

        RegionalEntity local = new RegionalEntity(1, "Regional Norte", true);
        when(regionalRepository.findByAtivoTrue()).thenReturn(List.of(local));

        // WHEN
        RegionalSyncResult result = service.sync();

        // THEN
        assertEquals(0, result.getInserted());
        assertEquals(1, result.getInactivated());
        assertEquals(1, result.getRecreated());
        
        assertFalse(local.getAtivo());
        
        ArgumentCaptor<RegionalEntity> captor = ArgumentCaptor.forClass(RegionalEntity.class);
        verify(regionalRepository, times(2)).save(captor.capture());
        
        List<RegionalEntity> savedEntities = captor.getAllValues();
        // Primeira chamada: inativar o antigo
        assertFalse(savedEntities.get(0).getAtivo());
        // Segunda chamada: criar o novo
        assertEquals("Regional Norte Alterada", savedEntities.get(1).getNome());
        assertTrue(savedEntities.get(1).getAtivo());
    }
}
