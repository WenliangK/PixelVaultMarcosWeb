-- Se ejecuta en cada arranque: reinicia el catalogo y los cupones.
DELETE FROM detalle_compra;
DELETE FROM compras;
DELETE FROM juegos;
DELETE FROM cupones;

INSERT INTO juegos (nombre, plataforma, plataforma_grupo, genero, desarrollador, anio_lanzamiento, precio, stock, imagen_url, descripcion) VALUES
('Counter-Strike 2', 'PC / Steam', 'pc', 'Shooter táctico', 'Valve', 2023, 58.00, 50, 'https://static.wikia.nocookie.net/logopedia/images/4/49/Counter-Strike_2_%28Icon%29.png/revision/latest?cb=20230330015359', 'La evolución del shooter táctico más jugado del mundo, con físicas renovadas, mapas modernizados y partidas competitivas de alta precisión.'),
('Minecraft', 'Xbox Series X', 'xbox', 'Sandbox / Mundo abierto', 'Mojang Studios', 2011, 110.00, 50, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRd5gr2YHx65IVjx04i5nnj9PrYbAayydNipA&s', 'Construye, explora y sobrevive en un mundo de bloques que nunca se repite. El único límite es tu imaginación y la llegada de la noche.'),
('Mario Party', 'Nintendo Switch', 'nintendo', 'Fiesta / Multijugador', 'Nintendo', 2018, 150.00, 8, 'https://mario.wiki.gallery/images/thumb/9/90/Mario_Party_2021_Logo_.png/250px-Mario_Party_2021_Logo_.png', 'Compite con amigos y familia en tableros llenos de minijuegos, estrellas, desafíos y alianzas inesperadas.'),
('Resident Evil 4 - Remake', 'PC / Steam', 'pc', 'Survival Horror', 'Capcom', 2023, 170.00, 40, 'https://image.api.playstation.com/vulcan/ap/rnd/202207/2509/85p2Dwh5iDhUzRKe40QeNYh3.png', 'Leon S. Kennedy regresa en una reconstrucción completa del clásico, con gráficos modernos, tensión constante y combate más visceral.'),
('Halo: The Master Chief Collection', 'PS5 / Xbox', 'xbox', 'Shooter en primera persona', '343 Industries', 2019, 200.00, 35, 'https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/976730/header.jpg?t=1774466074', 'Toda la saga del Jefe Maestro reunida en una colección con campañas remasterizadas y multijugador competitivo.'),
('GTA V Online', 'PC', 'pc', 'Acción / Mundo abierto', 'Rockstar Games', 2013, 160.00, 60, 'https://static.wikia.nocookie.net/esgta/images/1/1b/Car%C3%A1tula_GTA_V.jpg/revision/latest/scale-to-width-down/1200?cb=20130402191528', 'Construye tu imperio criminal en Los Santos, organiza atracos y participa en un universo online en constante evolución.'),
('The Legend of Zelda: TOTK', 'Nintendo Switch', 'nintendo', 'Aventura', 'Nintendo', 2023, 280.00, 0, 'https://www.nintendo.com/eu/media/images/other_22/character_hubs/the_legend_of_zelda/16x9_Other_TheLegendOfZelda_Hub_image1600w.jpg', 'Explora Hyrule desde el cielo hasta sus profundidades usando nuevas habilidades para construir, combinar y resolver desafíos.'),
('FIFA 24', 'Multiplataforma', 'playstation', 'Deportes', 'EA Sports', 2023, 220.00, 30, '', 'Competición de fútbol con clubes, ligas y modos de juego para disfrutar solo o en línea.'),
('Elden Ring', 'PlayStation 5', 'playstation', 'RPG de acción', 'FromSoftware', 2022, 250.00, 10, 'https://www.allkeyshop.com/blog/wp-content/uploads/elden-ring-title-1024x536.webp', 'Un vasto mundo de fantasía oscura, combate exigente y libertad total para construir tu propia aventura.'),
('God of War Ragnarök', 'PlayStation 5', 'playstation', 'Acción y aventura', 'Santa Monica Studio', 2022, 270.00, 25, 'https://image.api.playstation.com/vulcan/ap/rnd/202207/1210/4xJ8XB3bi888QTLZYdl7Oi0s.png', 'Kratos y Atreus recorren los Nueve Reinos en una aventura épica marcada por decisiones, combates y profecías.');

INSERT INTO cupones (codigo, porcentaje_descuento, fecha_expiracion, activo) VALUES
('PIXEL10', 10.00, NULL, true),
('GAMER20', 20.00, NULL, true),
('VAULT15', 15.00, NULL, true);
