CREATE TABLE artist (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(20) NOT NULL, -- SINGER | BAND
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Relacionamento N:N
CREATE TABLE artist_album (
    artist_id BIGINT NOT NULL,
    album_id BIGINT NOT NULL,
    PRIMARY KEY (artist_id, album_id),
    CONSTRAINT fk_artist_album_artist
        FOREIGN KEY (artist_id) REFERENCES artist(id),
    CONSTRAINT fk_artist_album_album
        FOREIGN KEY (album_id) REFERENCES album(id)
);

-- Imagens de capa do álbum (MinIO)
CREATE TABLE album_image (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL,
    object_key VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    size BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_album_image_album
        FOREIGN KEY (album_id) REFERENCES album(id)
);

-- Refresh Token (JWT)
CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    token VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);