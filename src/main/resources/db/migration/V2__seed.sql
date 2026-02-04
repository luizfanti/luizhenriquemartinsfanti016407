-- ARTISTAS
INSERT INTO artist (name, type) VALUES
('Serj Tankian', 'SINGER'),
('Mike Shinoda', 'SINGER'),
('Michel Teló', 'SINGER'),
('Guns N'' Roses', 'BAND');

-- ÁLBUNS
INSERT INTO album (title) VALUES
('Harakiri'),
('Black Blooms'),
('The Rough Dog'),

('The Rising Tied'),
('Post Traumatic'),
('Post Traumatic EP'),
('Where''d You Go'),

('Bem Sertanejo'),
('Bem Sertanejo - O Show (Ao Vivo)'),
('Bem Sertanejo - (1ª Temporada) - EP'),

('Use Your Illusion I'),
('Use Your Illusion II'),
('Greatest Hits');

-- RELACIONAMENTOS (N:N) sem depender de IDs fixos
-- Serj Tankian
INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id FROM artist a, album al
WHERE a.name = 'Serj Tankian' AND al.title IN ('Harakiri','Black Blooms','The Rough Dog');

-- Mike Shinoda
INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id FROM artist a, album al
WHERE a.name = 'Mike Shinoda' AND al.title IN ('The Rising Tied','Post Traumatic','Post Traumatic EP','Where''d You Go');

-- Michel Teló
INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id FROM artist a, album al
WHERE a.name = 'Michel Teló' AND al.title IN ('Bem Sertanejo','Bem Sertanejo - O Show (Ao Vivo)','Bem Sertanejo - (1ª Temporada) - EP');

-- Guns N' Roses
INSERT INTO artist_album (artist_id, album_id)
SELECT a.id, al.id FROM artist a, album al
WHERE a.name = 'Guns N'' Roses' AND al.title IN ('Use Your Illusion I','Use Your Illusion II','Greatest Hits');
