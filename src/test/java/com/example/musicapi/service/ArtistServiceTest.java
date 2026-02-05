package com.example.musicapi.service;

import com.example.musicapi.dto.ArtistCreateRequest;
import com.example.musicapi.dto.ArtistResponse;
import com.example.musicapi.exception.NotFoundException;
import com.example.musicapi.model.ArtistEntity;
import com.example.musicapi.model.ArtistType;
import com.example.musicapi.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    private ArtistService service;

    @BeforeEach
    void setUp() {
        service = new ArtistService(artistRepository);
    }

    @Test
    void create_ShouldSaveArtist() {
        ArtistCreateRequest req = new ArtistCreateRequest();
        req.setName("Serj Tankian");
        req.setType(ArtistType.SINGER);

        ArtistEntity entity = new ArtistEntity("Serj Tankian", ArtistType.SINGER);
        entity.setId(1L);

        when(artistRepository.save(any())).thenReturn(entity);

        ArtistResponse res = service.create(req);

        assertNotNull(res.getId());
        assertEquals("Serj Tankian", res.getName());
        verify(artistRepository).save(any());
    }

    @Test
    void getById_ShouldThrowNotFound() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void searchByName_ShouldReturnList() {
        ArtistEntity entity = new ArtistEntity("Mike Shinoda", ArtistType.SINGER);
        when(artistRepository.findByNameContainingIgnoreCase(eq("Mike"), any(Sort.class)))
                .thenReturn(List.of(entity));

        List<ArtistResponse> results = service.searchByName("Mike", "asc");

        assertEquals(1, results.size());
        assertEquals("Mike Shinoda", results.get(0).getName());
    }
}
