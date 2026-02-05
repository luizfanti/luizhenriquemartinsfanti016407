package com.example.musicapi.service;

import com.example.musicapi.dto.AlbumCreateRequest;
import com.example.musicapi.dto.AlbumResponse;
import com.example.musicapi.model.AlbumEntity;
import com.example.musicapi.model.ArtistEntity;
import com.example.musicapi.model.ArtistType;
import com.example.musicapi.repository.AlbumRepository;
import com.example.musicapi.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private AlbumNotificationService notificationService;

    private AlbumService service;

    @BeforeEach
    void setUp() {
        service = new AlbumService(albumRepository, artistRepository, notificationService);
    }

    @Test
    void create_ShouldSaveAlbumAndNotify() {
        AlbumCreateRequest req = new AlbumCreateRequest();
        req.setTitle("Harakiri");
        req.setArtistIds(Set.of(1L));

        ArtistEntity artist = new ArtistEntity("Serj Tankian", ArtistType.SINGER);
        artist.setId(1L);

        AlbumEntity album = new AlbumEntity("Harakiri");
        album.setId(10L);
        album.setArtists(Set.of(artist));

        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any())).thenReturn(album);

        AlbumResponse res = service.create(req);

        assertEquals("Harakiri", res.getTitle());
        assertEquals(1, res.getArtists().size());
        verify(albumRepository).save(any());
        verify(notificationService).notifyAlbumCreated(any(), any());
    }
}
