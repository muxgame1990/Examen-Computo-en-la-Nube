
USE computo_examen;

INSERT INTO productos
  (identificador, nombreProducto, descripcion, precio, stock, categoria, imagenProducto)
VALUES
-- Perifericos
('PER-001', 'Mouse Logitech G502 HERO',      'Mouse gamer alambrico, sensor HERO 25K y 11 botones programables.', 899.00, 25, 'perifericos',
 'https://placehold.co/600x400/f97316/ffffff?text=Mouse+G502'),
('PER-002', 'Teclado Redragon Kumara K552',  'Teclado mecanico TKL, switches rojos y retroiluminacion RGB.',       749.00, 30, 'perifericos',
 'https://placehold.co/600x400/f97316/ffffff?text=Teclado+Kumara'),
('PER-003', 'Audifonos HyperX Cloud II',     'Audifonos gamer con sonido 7.1 virtual y microfono desmontable.',   1599.00, 12, 'perifericos',
 'https://placehold.co/600x400/f97316/ffffff?text=HyperX+Cloud+II'),
('PER-004', 'Monitor ASUS 24 pulgadas 165Hz', 'Monitor Full HD IPS de 24 pulgadas, 165 Hz y 1 ms.',               3299.00,  4, 'perifericos',
 'https://placehold.co/600x400/f97316/ffffff?text=Monitor+ASUS'),

-- Tarjetas de video
('GPU-001', 'NVIDIA GeForce RTX 4060 8GB',   'Tarjeta de video RTX 4060 con 8 GB GDDR6 y DLSS 3.',              6499.00,  8, 'tarjetas de video',
 'https://placehold.co/600x400/dc2626/ffffff?text=RTX+4060'),
('GPU-002', 'AMD Radeon RX 7600 8GB',        'Tarjeta de video RX 7600 con 8 GB GDDR6 para 1080p.',             5299.00,  6, 'tarjetas de video',
 'https://placehold.co/600x400/dc2626/ffffff?text=RX+7600'),
('GPU-003', 'NVIDIA GeForce RTX 4070 12GB',  'Tarjeta de video RTX 4070 con 12 GB GDDR6X para 1440p.',          11999.00,  3, 'tarjetas de video',
 'https://placehold.co/600x400/dc2626/ffffff?text=RTX+4070'),

-- Procesadores
('CPU-001', 'AMD Ryzen 5 7600',              'Procesador de 6 nucleos y 12 hilos, socket AM5, hasta 5.1 GHz.',   3899.00, 12, 'procesadores',
 'https://placehold.co/600x400/ea580c/ffffff?text=Ryzen+5+7600'),
('CPU-002', 'Intel Core i5-12400F',          'Procesador de 6 nucleos y 12 hilos, LGA1700, sin graficos.',       2499.00, 15, 'procesadores',
 'https://placehold.co/600x400/ea580c/ffffff?text=i5-12400F'),
('CPU-003', 'AMD Ryzen 7 7800X3D',           'Procesador de 8 nucleos con 3D V-Cache, ideal para gaming.',       8999.00,  2, 'procesadores',
 'https://placehold.co/600x400/ea580c/ffffff?text=Ryzen+7+7800X3D'),

-- Memoria RAM
('RAM-001', 'Kingston Fury Beast 16GB DDR5', 'Kit 2x8 GB DDR5 a 5200 MHz, CL40.',                                1199.00, 40, 'memoria ram',
 'https://placehold.co/600x400/b91c1c/ffffff?text=Fury+DDR5'),
('RAM-002', 'Corsair Vengeance 32GB DDR4',   'Kit 2x16 GB DDR4 a 3200 MHz, CL16.',                               1599.00, 20, 'memoria ram',
 'https://placehold.co/600x400/b91c1c/ffffff?text=Vengeance+DDR4'),
('RAM-003', 'TeamGroup T-Force 8GB DDR4',    'Modulo de 8 GB DDR4 a 3200 MHz con disipador.',                     449.00,  0, 'memoria ram',
 'https://placehold.co/600x400/b91c1c/ffffff?text=T-Force+DDR4'),

-- Accesorios
('ACC-001', 'Pasta termica Arctic MX-4 4g',  'Pasta termica de alto rendimiento, jeringa de 4 gramos.',            179.00, 60, 'accesorios',
 'https://placehold.co/600x400/fb923c/ffffff?text=Arctic+MX-4'),
('ACC-002', 'Cable HDMI 2.1 de 2 metros',    'Cable HDMI 2.1 para 4K a 120 Hz y 8K a 60 Hz.',                     249.00,  5, 'accesorios',
 'https://placehold.co/600x400/fb923c/ffffff?text=HDMI+2.1'),
('ACC-003', 'Mousepad XL Redragon',          'Mousepad extendido de 80x30 cm con base antiderrapante.',            299.00, 35, 'accesorios',
 'https://placehold.co/600x400/fb923c/ffffff?text=Mousepad+XL');
