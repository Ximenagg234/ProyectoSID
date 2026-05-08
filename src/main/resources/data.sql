-- =========================
-- ESTADO
-- =========================
INSERT INTO estado (id_estado, nombre) VALUES (1, 'ACTIVO');
INSERT INTO estado (id_estado, nombre) VALUES (2, 'INACTIVO');
INSERT INTO estado (id_estado, nombre) VALUES (3, 'PENDIENTE');
INSERT INTO estado (id_estado, nombre) VALUES (4, 'CONFIRMADO');
INSERT INTO estado (id_estado, nombre) VALUES (5, 'PREPARANDO');
INSERT INTO estado (id_estado, nombre) VALUES (6, 'ENTREGADO');
INSERT INTO estado (id_estado, nombre) VALUES (7, 'CANCELADO');

-- =========================
-- CATEGORIA
-- =========================
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (1, 'Tecnologia', 'Productos y servicios tecnologicos');
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (2, 'Moda', 'Ropa y accesorios');
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (3, 'Comida', 'Alimentos y snacks');
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (4, 'Bebidas', 'Bebidas y cafes');
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (5, 'Arte', 'Productos artisticos y creativos');
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (6, 'Servicios', 'Servicios y asesorias');

-- =========================
-- SEMESTRE
-- =========================
INSERT INTO semestre (id_semestre, periodo, fecha_inicio, fecha_fin, id_estado)
VALUES (1, '2026-1', CURRENT_DATE, CURRENT_DATE, 1);

-- =========================
-- ROLES
-- =========================
INSERT INTO rol (id_rol, nombre) VALUES (1, 'ADMIN');
INSERT INTO rol (id_rol, nombre) VALUES (2, 'EMPRENDEDOR');
INSERT INTO rol (id_rol, nombre) VALUES (3, 'COMPRADOR');

-- =========================
-- PERMISSIONS
-- =========================
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (1, 'CREAR_USUARIO', 'Crear usuarios');
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (2, 'VER_PRODUCTOS', 'Ver productos');
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (3, 'CREAR_PRODUCTO', 'Crear productos');
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (4, 'ELIMINAR_PRODUCTO', 'Eliminar productos');

-- =========================
-- ROLE_PERMISSION
-- =========================
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 1);
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 2);
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 3);
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 4);
INSERT INTO role_permission (id_rol, id_permission) VALUES (2, 2);
INSERT INTO role_permission (id_rol, id_permission) VALUES (2, 3);
INSERT INTO role_permission (id_rol, id_permission) VALUES (2, 4);
INSERT INTO role_permission (id_rol, id_permission) VALUES (3, 2);

-- ===============================================================
-- USUARIOS  (clave: 1234 para todos)
-- ===============================================================
INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (1, 'Ximena Gomez', 'ximena@icesi.edu.co', 'Ingenieria de Sistemas', 6, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (2, 'Carlos Perez', 'carlos@icesi.edu.co', 'Ingenieria de Sistemas', 5, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (3, 'Ana Martinez', 'ana@icesi.edu.co', 'Diseno Industrial', 7, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (4, 'Juan Restrepo', 'juan@icesi.edu.co', 'Administracion de Empresas', 4, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (5, 'Sofia Ramirez', 'sofia@icesi.edu.co', 'Gastronomia', 3, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (6, 'Miguel Torres', 'miguel@icesi.edu.co', 'Ingenieria de Sistemas', 8, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (7, 'Laura Sanchez', 'laura@icesi.edu.co', 'Psicologia', 5, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (8, 'David Gomez', 'david@icesi.edu.co', 'Comunicacion Social', 2, NULL,
        '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

-- =========================
-- USER_ROLE
-- =========================
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 1);  -- ximena: ADMIN
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 2);  -- ximena: EMPRENDEDOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (2, 2);  -- carlos: EMPRENDEDOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (3, 2);  -- ana: EMPRENDEDOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (4, 3);  -- juan: COMPRADOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (5, 2);  -- sofia: EMPRENDEDOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (6, 2);  -- miguel: EMPRENDEDOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (6, 3);  -- miguel: COMPRADOR tambien
INSERT INTO user_role (id_usuario, id_rol) VALUES (7, 3);  -- laura: COMPRADOR
INSERT INTO user_role (id_usuario, id_rol) VALUES (8, 3);  -- david: COMPRADOR

-- ===============================================================
-- EMPRENDIMIENTOS
-- ===============================================================
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (1, 'TechStore', 'Accesorios y gadgets tecnologicos de calidad para estudiantes de Icesi. Celulares, audifonos y perifericos al mejor precio.', NULL, TRUE, 1, 1, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (2, 'IcesiWear', 'Ropa urbana y accesorios de moda universitaria. Disenos exclusivos pensados para el estudiante de Icesi.', NULL, TRUE, 2, 2, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (3, 'CafeIcesi', 'Cafe de especialidad preparado con amor por estudiantes de Icesi. Granos seleccionados y preparaciones artesanales.', NULL, TRUE, 3, 4, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (4, 'ArteU', 'Ilustraciones, productos artisticos y personalizados con identidad universitaria. Arte hecho por estudiantes para estudiantes.', NULL, FALSE, 3, 5, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (5, 'FoodLab Icesi', 'Comida saludable y deliciosa preparada por estudiantes de Gastronomia. Bowls, wraps y postres artesanales cada semana.', NULL, TRUE, 5, 3, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (6, 'Manos Creativas', 'Manualidades, ceramica y productos hechos a mano con materiales sostenibles. Cada pieza es unica e irrepetible.', NULL, FALSE, 5, 5, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (7, 'CodeByte Solutions', 'Servicios de desarrollo web, apps y soporte tecnico para la comunidad universitaria. Soluciones digitales accesibles.', NULL, FALSE, 6, 6, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (8, 'GreenFit', 'Suplementos naturales, proteinas vegetales y snacks fitness para estudiantes activos. Salud y energia para tu dia.', NULL, FALSE, 2, 3, 1, 1);

-- ===============================================================
-- PRODUCTOS
-- ===============================================================

-- TechStore (1) — Tecnologia
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (1, 'iPhone 13 Reacondicionado', 'iPhone 13 en perfecto estado, bateria al 92%, incluye cargador y estuche protector.', 3500000, 3, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (2, 'AirPods Pro 2da Gen', 'Audifonos Apple originales con cancelacion de ruido activa y estuche de carga inalambrica.', 980000, 6, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (3, 'Cargador USB-C 65W', 'Cargador rapido GaN compatible con MacBook, iPad y la mayoria de laptops y celulares modernos.', 48000, 20, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (4, 'Mouse Logitech MX Master 3', 'Mouse ergonomico de alta precision con scroll rapido. Ideal para largas horas de estudio y trabajo.', 180000, 8, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (5, 'Hub USB-C 7 en 1', 'Hub multipuerto con HDMI 4K, 3x USB-A, SD, microSD y USB-C PD 100W. Perfecto para MacBook.', 85000, 12, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (6, 'Soporte Laptop Ajustable', 'Soporte de aluminio ergonomico con 6 niveles de altura. Reduce la fatiga en el cuello y espalda.', 62000, 0, 1, 2);

-- IcesiWear (2) — Moda
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (7, 'Camiseta Oversize Bordada', 'Camiseta 100% algodon con bordado exclusivo del logo Icesi. Disponible en 4 colores.', 55000, 28, 2, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (8, 'Tote Bag Canvas Premium', 'Bolsa de lona reforzada con estampado universitario. Perfecta para el dia a dia en el campus.', 38000, 15, 2, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (9, 'Hoodie Universitario', 'Sudadera premium con capucha, bolsillo canguro y logo bordado. Unisex, tallas S-XL.', 95000, 10, 2, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (10, 'Gorra Snapback Icesi', 'Gorra estilo snapback con parche bordado frontal. Ajuste universal, materiales premium.', 42000, 20, 2, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (11, 'Medias Estampadas x3', 'Pack de 3 pares de medias con disenos geometricos exclusivos IcesiWear. Talla unica.', 25000, 0, 2, 2);

-- CafeIcesi (3) — Bebidas
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (12, 'Cappuccino Especial', 'Doble espresso con leche de avena espumada y un toque de canela. El favorito del campus.', 9500, 50, 3, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (13, 'Latte de Caramelo', 'Espresso suave con leche entera y sirope artesanal de caramelo. Caliente o frio.', 10500, 40, 3, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (14, 'Cold Brew 500ml', 'Cafe de filtracion en frio por 18 horas. Sabor intenso y suave al mismo tiempo. Sin azucar.', 12000, 25, 3, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (15, 'Pack Brownies x6', 'Brownies artesanales de chocolate belga 70%. Sin conservantes. Recien horneados cada manana.', 28000, 18, 3, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (16, 'Smoothie Tropical', 'Mezcla de mango, maracuya y mora con base de leche de coco. Energizante y refrescante.', 11000, 30, 3, 1);

-- ArteU (4) — Arte
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (17, 'Sticker Pack Universitario x10', 'Set de 10 stickers premium con ilustraciones tematicas de Icesi y la vida universitaria.', 12000, 60, 4, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (18, 'Ilustracion Personalizada', 'Retrato digital en estilo caricatura expresiva. Entrega en formato PNG HD en 3 dias habiles.', 45000, 10, 4, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (19, 'Poster Arte Universitario A3', 'Poster de alta calidad impreso en papel satinado 200g. Disenos exclusivos de la coleccion 2026.', 22000, 25, 4, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (20, 'Cuaderno Ilustrado A5', 'Cuaderno de 120 paginas con portada ilustrada a mano. Papel de 90g, ideal para apuntes y bocetos.', 18000, 35, 4, 1);

-- FoodLab Icesi (5) — Comida
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (21, 'Bowl Proteico Pollo', 'Bowl de quinoa, pollo al limon, aguacate, tomate cherry y aderezo de yogur. 450kcal aprox.', 18000, 20, 5, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (22, 'Wrap Vegano', 'Tortilla integral con hummus, zanahorias ralladas, pepino, espinacas y germinados. 380kcal.', 14000, 15, 5, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (23, 'Cheesecake de Frutos Rojos', 'Porcion de cheesecake horneado con mermelada artesanal de frutos rojos. Sin gluten.', 9000, 12, 5, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (24, 'Granola Artesanal 300g', 'Mezcla de avena tostada, miel, nueces, arrandanos y semillas de chia. Sin azucar refinada.', 16000, 30, 5, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (25, 'Muffins Integrales x4', 'Muffins de platano y avena sin azucar. Endulzados naturalmente con datiles. Pack de 4 unidades.', 12000, 0, 5, 2);

-- Manos Creativas (6) — Arte
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (26, 'Taza Ceramica Artesanal', 'Taza de ceramica moldeada y pintada a mano. Cada pieza es unica. Capacidad 350ml. Apta para microondas.', 35000, 8, 6, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (27, 'Maceta Colgante Tejida', 'Maceta en macrame tejida a mano con cuerda de yute natural. Ideal para suculentas y plantas pequenas.', 28000, 12, 6, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (28, 'Vela Aromatica Soya', 'Vela de cera de soya con aceites esenciales de lavanda y eucalipto. Recipiente de vidrio reutilizable. 40h de duracion.', 22000, 20, 6, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (29, 'Pulsera Hilo Bordado', 'Pulsera tejida a mano con hilos de colores y dijes de plata. Personalizable con inicial.', 15000, 25, 6, 1);

-- CodeByte Solutions (7) — Servicios
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (30, 'Pagina Web Basica', 'Landing page responsiva con hasta 5 secciones, formulario de contacto y despliegue en hosting. Entrega en 7 dias.', 250000, 5, 7, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (31, 'Tutoria Programacion 2h', 'Sesion personalizada de 2 horas en Python, Java o JavaScript. Estudiante con 4 semestres de experiencia.', 40000, 20, 7, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (32, 'Reparacion PC / Laptop', 'Diagnostico y reparacion de equipos. Limpieza interna, actualizacion de software y eliminacion de virus.', 60000, 10, 7, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (33, 'Edicion Video Corto', 'Edicion de video de hasta 3 minutos con musica, transiciones y subtitulos. Entrega en 48 horas.', 55000, 8, 7, 1);

-- GreenFit (8) — Comida
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (34, 'Proteina Vegetal Vainilla 500g', 'Proteina de guisante y arroz integral con 24g de proteina por porcion. Sin lactosa ni gluten.', 85000, 15, 8, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (35, 'Pack Snacks Saludables x5', 'Seleccion de 5 snacks: nueces mixtas, chips de kale, barra de dattiles, almendras y semillas.', 32000, 22, 8, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (36, 'Collageno en Polvo 200g', 'Colageno marino hidrolizado sin sabor. Se disuelve en cualquier bebida. 30 porciones.', 65000, 10, 8, 1);

-- ===============================================================
-- IMAGENES DE PRODUCTO (loremflickr.com - imagenes reales por categoria)
-- ===============================================================
-- TechStore
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (1,  'https://loremflickr.com/400/300/iphone,smartphone', 1);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (2,  'https://loremflickr.com/400/300/iphone,screen', 1);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (3,  'https://loremflickr.com/400/300/airpods,earbuds', 2);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (4,  'https://loremflickr.com/400/300/usb,charger', 3);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (5,  'https://loremflickr.com/400/300/mouse,logitech', 4);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (6,  'https://loremflickr.com/400/300/computer,mouse', 4);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (7,  'https://loremflickr.com/400/300/usb,hub', 5);
-- IcesiWear
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (8,  'https://loremflickr.com/400/300/tshirt,fashion', 7);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (9,  'https://loremflickr.com/400/300/oversize,shirt', 7);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (10, 'https://loremflickr.com/400/300/tote,bag', 8);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (11, 'https://loremflickr.com/400/300/hoodie,sweatshirt', 9);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (12, 'https://loremflickr.com/400/300/snapback,cap', 10);
-- CafeIcesi
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (13, 'https://loremflickr.com/400/300/cappuccino,coffee', 12);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (14, 'https://loremflickr.com/400/300/espresso,foam', 12);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (15, 'https://loremflickr.com/400/300/latte,caramel', 13);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (16, 'https://loremflickr.com/400/300/cold,brew', 14);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (17, 'https://loremflickr.com/400/300/brownie,chocolate', 15);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (18, 'https://loremflickr.com/400/300/smoothie,tropical', 16);
-- ArteU
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (19, 'https://loremflickr.com/400/300/stickers,colorful', 17);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (20, 'https://loremflickr.com/400/300/illustration,portrait', 18);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (21, 'https://loremflickr.com/400/300/poster,art', 19);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (22, 'https://loremflickr.com/400/300/notebook,journal', 20);
-- FoodLab Icesi
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (23, 'https://loremflickr.com/400/300/bowl,chicken', 21);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (24, 'https://loremflickr.com/400/300/quinoa,avocado', 21);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (25, 'https://loremflickr.com/400/300/wrap,vegan', 22);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (26, 'https://loremflickr.com/400/300/cheesecake,berries', 23);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (27, 'https://loremflickr.com/400/300/granola,oats', 24);
-- Manos Creativas
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (28, 'https://loremflickr.com/400/300/ceramic,mug', 26);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (29, 'https://loremflickr.com/400/300/pottery,handmade', 26);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (30, 'https://loremflickr.com/400/300/macrame,plant', 27);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (31, 'https://loremflickr.com/400/300/candle,soy', 28);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (32, 'https://loremflickr.com/400/300/bracelet,handmade', 29);
-- CodeByte Solutions
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (33, 'https://loremflickr.com/400/300/website,laptop', 30);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (34, 'https://loremflickr.com/400/300/programming,code', 31);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (35, 'https://loremflickr.com/400/300/computer,repair', 32);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (36, 'https://loremflickr.com/400/300/video,editing', 33);
-- GreenFit
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (37, 'https://loremflickr.com/400/300/protein,powder', 34);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (38, 'https://loremflickr.com/400/300/snacks,healthy', 35);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (39, 'https://loremflickr.com/400/300/collagen,supplement', 36);

-- ===============================================================
-- PEDIDOS  (todos los estados representados)
-- ===============================================================
-- Pedido 1: Juan → IcesiWear — CONFIRMADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (1, DATEADD('DAY', -12, CURRENT_TIMESTAMP), 93000, 4, 2, 4);

-- Pedido 2: Juan → TechStore — ENTREGADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (2, DATEADD('DAY', -20, CURRENT_TIMESTAMP), 228000, 4, 1, 6);

-- Pedido 3: Juan → CafeIcesi — ENTREGADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (3, DATEADD('DAY', -15, CURRENT_TIMESTAMP), 47500, 4, 3, 6);

-- Pedido 4: Laura → TechStore — PENDIENTE
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (4, DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 980000, 7, 1, 3);

-- Pedido 5: Laura → IcesiWear — PREPARANDO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (5, DATEADD('DAY', -2, CURRENT_TIMESTAMP), 55000, 7, 2, 5);

-- Pedido 6: David → ArteU — ENTREGADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (6, DATEADD('DAY', -30, CURRENT_TIMESTAMP), 57000, 8, 4, 6);

-- Pedido 7: David → FoodLab — CANCELADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (7, DATEADD('DAY', -5, CURRENT_TIMESTAMP), 32000, 8, 5, 7);

-- Pedido 8: Miguel → CafeIcesi — ENTREGADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (8, DATEADD('DAY', -18, CURRENT_TIMESTAMP), 57000, 6, 3, 6);

-- Pedido 9: Miguel → CodeByte — PENDIENTE
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (9, DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 40000, 6, 7, 3);

-- Pedido 10: Juan → FoodLab — ENTREGADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (10, DATEADD('DAY', -8, CURRENT_TIMESTAMP), 50000, 4, 5, 6);

-- Pedido 11: Laura → CafeIcesi — ENTREGADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (11, DATEADD('DAY', -22, CURRENT_TIMESTAMP), 38000, 7, 3, 6);

-- Pedido 12: Miguel → TechStore — CONFIRMADO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (12, DATEADD('DAY', -1, CURRENT_TIMESTAMP), 48000, 6, 1, 4);

-- ===============================================================
-- DETALLE_PEDIDO
-- ===============================================================
-- Pedido 1 (Juan → IcesiWear)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (1, 1, 55000, 55000, 1, 7);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (2, 1, 38000, 38000, 1, 8);

-- Pedido 2 (Juan → TechStore)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (3, 1, 180000, 180000, 2, 4);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (4, 1, 48000, 48000, 2, 3);

-- Pedido 3 (Juan → CafeIcesi)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (5, 3, 9500, 28500, 3, 12);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (6, 1, 19000, 19000, 3, 15);

-- Pedido 4 (Laura → TechStore)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (7, 1, 980000, 980000, 4, 2);

-- Pedido 5 (Laura → IcesiWear)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (8, 1, 55000, 55000, 5, 7);

-- Pedido 6 (David → ArteU)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (9, 1, 45000, 45000, 6, 18);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (10, 1, 12000, 12000, 6, 17);

-- Pedido 7 (David → FoodLab) - CANCELADO
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (11, 2, 16000, 32000, 7, 24);

-- Pedido 8 (Miguel → CafeIcesi)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (12, 3, 9500, 28500, 8, 12);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (13, 1, 28000, 28000, 8, 15);

-- Pedido 9 (Miguel → CodeByte)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (14, 1, 40000, 40000, 9, 31);

-- Pedido 10 (Juan → FoodLab)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (15, 1, 18000, 18000, 10, 21);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (16, 1, 14000, 14000, 10, 22);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (17, 2, 9000, 18000, 10, 23);

-- Pedido 11 (Laura → CafeIcesi)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (18, 2, 10500, 21000, 11, 13);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (19, 1, 12000, 12000, 11, 14);

-- Pedido 12 (Miguel → TechStore)
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (20, 1, 48000, 48000, 12, 3);

-- ===============================================================
-- CALIFICACIONES (solo para pedidos ENTREGADOS: 2,3,6,8,10,11)
-- ===============================================================
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (1, 5, 'Excelente servicio! El mouse llego perfecto y el envio fue rapidisimo. Muy recomendado.', DATEADD('DAY', -19, CURRENT_TIMESTAMP), 4, 1, 2);

INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (2, 5, 'El cappuccino esta increible, mejor que el de las cafeterias normales. Ya es mi favorito del campus!', DATEADD('DAY', -14, CURRENT_TIMESTAMP), 4, 3, 3);

INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (3, 4, 'Los stickers quedaron hermosos y la ilustracion personalizada supero mis expectativas. Muy talentosa.', DATEADD('DAY', -28, CURRENT_TIMESTAMP), 8, 4, 6);

INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (4, 5, 'Compro seguido en CafeIcesi, siempre fresco y delicioso. El cold brew es adictivo.', DATEADD('DAY', -17, CURRENT_TIMESTAMP), 6, 3, 8);

INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (5, 4, 'Muy rico el bowl proteico, justo lo que necesitaba. La porcion es generosa. Volveria a pedir.', DATEADD('DAY', -7, CURRENT_TIMESTAMP), 4, 5, 10);

INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (6, 3, 'El cafe estaba bueno pero la entrega se demoro mas de lo prometido. Mejora en los tiempos.', DATEADD('DAY', -21, CURRENT_TIMESTAMP), 7, 3, 11);

-- ===============================================================
-- NOTIFICACIONES
-- ===============================================================
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (1, 'PEDIDO', 'Nuevo pedido recibido en IcesiWear', DATEADD('DAY', -12, CURRENT_TIMESTAMP), 2, 1);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (2, 'PEDIDO', 'Nuevo pedido recibido en TechStore', DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 1, 4);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (3, 'PEDIDO', 'Nuevo pedido recibido en IcesiWear', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 2, 5);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (4, 'PEDIDO', 'Tu pedido en TechStore fue confirmado', DATEADD('DAY', -19, CURRENT_TIMESTAMP), 4, 2);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (5, 'CALIFICACION', 'Nueva resena de 5 estrellas en TechStore', DATEADD('DAY', -19, CURRENT_TIMESTAMP), 1, 1);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (6, 'CALIFICACION', 'Nueva resena de 5 estrellas en CafeIcesi', DATEADD('DAY', -14, CURRENT_TIMESTAMP), 3, 2);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (7, 'PEDIDO', 'Nuevo pedido recibido en CodeByte', DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 6, 9);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (8, 'PEDIDO', 'Nuevo pedido en TechStore confirmado', DATEADD('DAY', -1, CURRENT_TIMESTAMP), 1, 12);

-- ===============================================================
-- REPORTE
-- ===============================================================
INSERT INTO reporte (id_reporte, fecha_generacion, periodo, ruta_archivo, id_usuario)
VALUES (1, CURRENT_TIMESTAMP, '2026-1', 'reportes/reporte_2026_1.pdf', 1);

-- ===============================================================
-- REINICIAR SECUENCIAS
-- ===============================================================
ALTER TABLE usuario         ALTER COLUMN id_usuario         RESTART WITH 200;
ALTER TABLE rol             ALTER COLUMN id_rol             RESTART WITH 200;
ALTER TABLE permission      ALTER COLUMN id_permission      RESTART WITH 200;
ALTER TABLE emprendimiento  ALTER COLUMN id_emprendimiento  RESTART WITH 200;
ALTER TABLE producto        ALTER COLUMN id_producto        RESTART WITH 200;
ALTER TABLE pedido          ALTER COLUMN id_pedido          RESTART WITH 200;
ALTER TABLE detalle_pedido  ALTER COLUMN id_detalle         RESTART WITH 200;
ALTER TABLE categoria       ALTER COLUMN id_categoria       RESTART WITH 200;
ALTER TABLE estado          ALTER COLUMN id_estado          RESTART WITH 200;
ALTER TABLE semestre        ALTER COLUMN id_semestre        RESTART WITH 200;
ALTER TABLE imagen_producto ALTER COLUMN id_imagen          RESTART WITH 200;
ALTER TABLE calificacion    ALTER COLUMN id_calificacion    RESTART WITH 200;
ALTER TABLE reporte         ALTER COLUMN id_reporte         RESTART WITH 200;
ALTER TABLE notificacion    ALTER COLUMN id_notificacion    RESTART WITH 200;
