-- ARTISTAS
INSERT INTO artist (name, type) VALUES
('Serj Tankian', 'SINGER'),
('Mike Shinoda', 'SINGER'),
('Michel Teló', 'SINGER'),
('Guns N’ Roses', 'BAND');

-- ÁLBUNS
INSERT INTO album (title) VALUES
('Harakiri'),
('Black Blooms'),
('The Rough Dog'),

('The Rising Tied'),
('Post Traumatic'),
('Post Traumatic EP'),
('Where’d You Go'),

('Bem Sertanejo'),
('Bem Sertanejo - O Show (Ao Vivo)'),
('Bem Sertanejo - (1ª Temporada) - EP'),

('Use Your Illusion I'),
('Use Your Illusion II'),
('Greatest Hits');

-- RELACIONAMENTOS
-- Serj Tankian
INSERT INTO artist_album VALUES
(1, 1),
(1, 2),
(1, 3);

-- Mike Shinoda
INSERT INTO artist_album VALUES
(2, 4),
(2, 5),
(2, 6),
(2, 7);

-- Michel Teló
INSERT INTO artist_album VALUES
(3, 8),
(3, 9),
(3, 10);

-- Guns N’ Roses
INSERT INTO artist_album VALUES
(4, 11),
(4, 12),
(4, 13);
