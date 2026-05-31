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
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 1);
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (2, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (3, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (4, 3);
INSERT INTO user_role (id_usuario, id_rol) VALUES (5, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (6, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (6, 3);
INSERT INTO user_role (id_usuario, id_rol) VALUES (7, 3);
INSERT INTO user_role (id_usuario, id_rol) VALUES (8, 3);

-- ===============================================================
-- EMPRENDIMIENTOS
-- ===============================================================
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (1, 'TechStore', 'Accesorios y gadgets tecnologicos de calidad para estudiantes de Icesi.',
        'https://images.unsplash.com/photo-1518770660439-4636190af475?w=300&h=300&fit=crop', TRUE, 1, 1, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (2, 'IcesiWear', 'Ropa urbana y accesorios de moda universitaria.',
        'https://images.unsplash.com/photo-1445205170230-053b83016050?w=300&h=300&fit=crop', TRUE, 2, 2, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (3, 'CafeIcesi', 'Cafe de especialidad preparado con amor por estudiantes de Icesi.',
        'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=300&h=300&fit=crop', TRUE, 3, 4, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (4, 'ArteU', 'Ilustraciones y productos artisticos con identidad universitaria.',
        'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=300&h=300&fit=crop', FALSE, 3, 5, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (5, 'FoodLab Icesi', 'Comida saludable preparada por estudiantes de Gastronomia.',
        'https://images.unsplash.com/photo-1547592180-85f173990554?w=300&h=300&fit=crop', TRUE, 5, 3, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (6, 'Manos Creativas', 'Manualidades y productos hechos a mano con materiales sostenibles.',
        'https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=300&h=300&fit=crop', FALSE, 5, 5, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (7, 'CodeByte Solutions', 'Servicios de desarrollo web y soporte tecnico para la comunidad universitaria.',
        'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=300&h=300&fit=crop', FALSE, 6, 6, 1, 1);
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, destacado, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (8, 'GreenFit', 'Suplementos naturales y snacks fitness para estudiantes activos.',
        'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=300&h=300&fit=crop', FALSE, 2, 3, 1, 1);

-- ===============================================================
-- PRODUCTOS
-- ===============================================================
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (1, 'iPhone 13 Reacondicionado', 'iPhone 13 en perfecto estado, bateria al 92%.', 3500000, 3, 1, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (2, 'AirPods Pro 2da Gen', 'Audifonos Apple con cancelacion de ruido activa.', 980000, 6, 1, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (3, 'Cargador USB-C 65W', 'Cargador rapido GaN compatible con MacBook y laptops.', 48000, 20, 1, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (4, 'Mouse Logitech MX Master 3', 'Mouse ergonomico de alta precision.', 180000, 8, 1, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (5, 'Hub USB-C 7 en 1', 'Hub multipuerto con HDMI 4K y USB-C PD 100W.', 85000, 12, 1, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (6, 'Soporte Laptop Ajustable', 'Soporte de aluminio ergonomico con 6 niveles.', 62000, 0, 1, 2);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (7, 'Camiseta Oversize Bordada', 'Camiseta 100% algodon con bordado exclusivo.', 55000, 28, 2, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (8, 'Tote Bag Canvas Premium', 'Bolsa de lona reforzada con estampado universitario.', 38000, 15, 2, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (9, 'Hoodie Universitario', 'Sudadera premium con capucha y logo bordado.', 95000, 10, 2, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (10, 'Gorra Snapback Icesi', 'Gorra con parche bordado frontal.', 42000, 20, 2, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (11, 'Medias Estampadas x3', 'Pack de 3 pares con disenos geometricos exclusivos.', 25000, 0, 2, 2);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (12, 'Cappuccino Especial', 'Doble espresso con leche de avena y canela.', 9500, 50, 3, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (13, 'Latte de Caramelo', 'Espresso con leche y sirope artesanal de caramelo.', 10500, 40, 3, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (14, 'Cold Brew 500ml', 'Cafe de filtracion en frio por 18 horas.', 12000, 25, 3, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (15, 'Pack Brownies x6', 'Brownies artesanales de chocolate belga 70%.', 28000, 18, 3, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (16, 'Smoothie Tropical', 'Mezcla de mango, maracuya y mora con leche de coco.', 11000, 30, 3, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (17, 'Sticker Pack Universitario x10', 'Set de 10 stickers con ilustraciones de Icesi.', 12000, 60, 4, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (18, 'Ilustracion Personalizada', 'Retrato digital en estilo caricatura expresiva.', 45000, 10, 4, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (19, 'Poster Arte Universitario A3', 'Poster impreso en papel satinado 200g.', 22000, 25, 4, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (20, 'Cuaderno Ilustrado A5', 'Cuaderno de 120 paginas con portada ilustrada.', 18000, 35, 4, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (21, 'Bowl Proteico Pollo', 'Bowl de quinoa, pollo al limon y aguacate.', 18000, 20, 5, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (22, 'Wrap Vegano', 'Tortilla integral con hummus y vegetales frescos.', 14000, 15, 5, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (23, 'Cheesecake de Frutos Rojos', 'Cheesecake horneado con mermelada artesanal.', 9000, 12, 5, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (24, 'Granola Artesanal 300g', 'Avena tostada con miel, nueces y arandanos.', 16000, 30, 5, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (25, 'Muffins Integrales x4', 'Muffins de platano y avena sin azucar.', 12000, 0, 5, 2);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (26, 'Taza Ceramica Artesanal', 'Taza moldeada y pintada a mano. Capacidad 350ml.', 35000, 8, 6, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (27, 'Maceta Colgante Tejida', 'Maceta en macrame con cuerda de yute natural.', 28000, 12, 6, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (28, 'Vela Aromatica Soya', 'Vela de cera de soya con lavanda y eucalipto.', 22000, 20, 6, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (29, 'Pulsera Hilo Bordado', 'Pulsera tejida con hilos de colores y dijes de plata.', 15000, 25, 6, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (30, 'Pagina Web Basica', 'Landing page responsiva con hasta 5 secciones.', 250000, 5, 7, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (31, 'Tutoria Programacion 2h', 'Sesion de 2 horas en Python, Java o JavaScript.', 40000, 20, 7, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (32, 'Reparacion PC / Laptop', 'Diagnostico y reparacion de equipos.', 60000, 10, 7, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (33, 'Edicion Video Corto', 'Edicion de video hasta 3 minutos con musica.', 55000, 8, 7, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (34, 'Proteina Vegetal Vainilla 500g', 'Proteina de guisante y arroz integral.', 85000, 15, 8, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (35, 'Pack Snacks Saludables x5', 'Seleccion de 5 snacks saludables variados.', 32000, 22, 8, 1);
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (36, 'Collageno en Polvo 200g', 'Colageno marino hidrolizado sin sabor.', 65000, 10, 8, 1);

-- ===============================================================
-- IMAGENES DE PRODUCTO
-- ===============================================================
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (1,  'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=300&fit=crop', 1);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (2,  'https://images.unsplash.com/photo-1603351154351-5e2d0600bb77?w=400&h=300&fit=crop', 2);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (3,  'https://images.unsplash.com/photo-1601524909162-ae8725290836?w=400&h=300&fit=crop', 3);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (4,  'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400&h=300&fit=crop', 4);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (5,  'https://images.unsplash.com/photo-1625842268584-8f3296236761?w=400&h=300&fit=crop', 5);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (6,  'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=300&fit=crop', 7);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (7,  'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400&h=300&fit=crop', 8);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (8,  'https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=400&h=300&fit=crop', 9);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (9,  'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400&h=300&fit=crop', 10);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (10, 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=300&fit=crop', 12);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (11, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop', 13);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (12, 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=400&h=300&fit=crop', 14);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (13, 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400&h=300&fit=crop', 15);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (14, 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=400&h=300&fit=crop', 16);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (15, 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400&h=300&fit=crop', 17);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (16, 'https://images.unsplash.com/photo-1605721911519-3dfeb3be25e7?w=400&h=300&fit=crop', 18);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (17, 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=400&h=300&fit=crop', 19);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (18, 'https://images.unsplash.com/photo-1531346878377-a5be20888e57?w=400&h=300&fit=crop', 20);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (19, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop', 21);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (20, 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop', 22);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (21, 'https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=400&h=300&fit=crop', 23);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (22, 'https://images.unsplash.com/photo-1686182689848-283fdd34e72f?w=400&h=300&fit=crop', 24);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (23, 'https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400&h=300&fit=crop', 26);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (24, 'https://images.unsplash.com/photo-1633594308237-3dcfa56b4e69?w=400&h=300&fit=crop', 27);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (25, 'https://images.unsplash.com/photo-1602607203588-d6d0eda790e3?w=400&h=300&fit=crop', 28);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (26, 'https://images.unsplash.com/photo-1611085583191-a3b181a88401?w=400&h=300&fit=crop', 29);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (27, 'https://images.unsplash.com/photo-1547658719-da2b51169166?w=400&h=300&fit=crop', 30);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (28, 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=400&h=300&fit=crop', 31);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (29, 'https://images.unsplash.com/photo-1597852074816-d933c7d2b988?w=400&h=300&fit=crop', 32);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (30, 'https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=400&h=300&fit=crop', 33);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (31, 'https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400&h=300&fit=crop', 34);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (32, 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&h=300&fit=crop', 35);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (33, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=300&fit=crop', 36);

-- ===============================================================
-- PEDIDOS  (PostgreSQL: intervalo con INTERVAL)
-- ===============================================================
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (1,  CURRENT_TIMESTAMP - INTERVAL '12 days',  93000,  4, 2, 4);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (2,  CURRENT_TIMESTAMP - INTERVAL '20 days', 228000,  4, 1, 6);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (3,  CURRENT_TIMESTAMP - INTERVAL '15 days',  47500,  4, 3, 6);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (4,  CURRENT_TIMESTAMP - INTERVAL '3 hours', 980000,  7, 1, 3);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (5,  CURRENT_TIMESTAMP - INTERVAL '2 days',   55000,  7, 2, 5);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (6,  CURRENT_TIMESTAMP - INTERVAL '30 days',  57000,  8, 4, 6);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (7,  CURRENT_TIMESTAMP - INTERVAL '5 days',   32000,  8, 5, 7);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (8,  CURRENT_TIMESTAMP - INTERVAL '18 days',  57000,  6, 3, 6);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (9,  CURRENT_TIMESTAMP - INTERVAL '1 hour',   40000,  6, 7, 3);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (10, CURRENT_TIMESTAMP - INTERVAL '8 days',   50000,  4, 5, 6);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (11, CURRENT_TIMESTAMP - INTERVAL '22 days',  38000,  7, 3, 6);
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (12, CURRENT_TIMESTAMP - INTERVAL '1 day',    48000,  6, 1, 4);

-- ===============================================================
-- DETALLE_PEDIDO
-- ===============================================================
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (1,  1, 55000,  55000,  1, 7);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (2,  1, 38000,  38000,  1, 8);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (3,  1,180000, 180000,  2, 4);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (4,  1, 48000,  48000,  2, 3);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (5,  3,  9500,  28500,  3, 12);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (6,  1, 19000,  19000,  3, 15);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (7,  1,980000, 980000,  4, 2);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (8,  1, 55000,  55000,  5, 7);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (9,  1, 45000,  45000,  6, 18);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (10, 1, 12000,  12000,  6, 17);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (11, 2, 16000,  32000,  7, 24);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (12, 3,  9500,  28500,  8, 12);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (13, 1, 28000,  28000,  8, 15);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (14, 1, 40000,  40000,  9, 31);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (15, 1, 18000,  18000, 10, 21);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (16, 1, 14000,  14000, 10, 22);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (17, 2,  9000,  18000, 10, 23);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (18, 2, 10500,  21000, 11, 13);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (19, 1, 12000,  12000, 11, 14);
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto) VALUES (20, 1, 48000,  48000, 12, 3);

-- ===============================================================
-- CALIFICACIONES
-- ===============================================================
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (1, 5, 'Excelente servicio! El mouse llego perfecto y el envio fue rapidisimo.',
        CURRENT_TIMESTAMP - INTERVAL '19 days', 4, 1, 2);
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (2, 5, 'El cappuccino esta increible, mejor que el de las cafeterias normales.',
        CURRENT_TIMESTAMP - INTERVAL '14 days', 4, 3, 3);
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (3, 4, 'Los stickers quedaron hermosos y la ilustracion personalizada supero mis expectativas.',
        CURRENT_TIMESTAMP - INTERVAL '28 days', 8, 4, 6);
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (4, 5, 'Compro seguido en CafeIcesi, siempre fresco y delicioso.',
        CURRENT_TIMESTAMP - INTERVAL '17 days', 6, 3, 8);
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (5, 4, 'Muy rico el bowl proteico, justo lo que necesitaba. La porcion es generosa.',
        CURRENT_TIMESTAMP - INTERVAL '7 days', 4, 5, 10);
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (6, 3, 'El cafe estaba bueno pero la entrega se demoro mas de lo prometido.',
        CURRENT_TIMESTAMP - INTERVAL '21 days', 7, 3, 11);

-- ===============================================================
-- NOTIFICACIONES
-- ===============================================================
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (1, 'PEDIDO',       'Nuevo pedido recibido en IcesiWear',          CURRENT_TIMESTAMP - INTERVAL '12 days', 2, 1);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (2, 'PEDIDO',       'Nuevo pedido recibido en TechStore',          CURRENT_TIMESTAMP - INTERVAL '3 hours',  1, 4);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (3, 'PEDIDO',       'Nuevo pedido recibido en IcesiWear',          CURRENT_TIMESTAMP - INTERVAL '2 days',  2, 5);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (4, 'PEDIDO',       'Tu pedido en TechStore fue confirmado',       CURRENT_TIMESTAMP - INTERVAL '19 days', 4, 2);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (5, 'CALIFICACION', 'Nueva resena de 5 estrellas en TechStore',   CURRENT_TIMESTAMP - INTERVAL '19 days', 1, 1);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (6, 'CALIFICACION', 'Nueva resena de 5 estrellas en CafeIcesi',   CURRENT_TIMESTAMP - INTERVAL '14 days', 3, 2);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (7, 'PEDIDO',       'Nuevo pedido recibido en CodeByte',           CURRENT_TIMESTAMP - INTERVAL '1 hour',  6, 9);
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (8, 'PEDIDO',       'Nuevo pedido en TechStore confirmado',        CURRENT_TIMESTAMP - INTERVAL '1 day',   1, 12);

-- ===============================================================
-- REPORTE
-- ===============================================================
INSERT INTO reporte (id_reporte, fecha_generacion, periodo, ruta_archivo, id_usuario)
VALUES (1, CURRENT_TIMESTAMP, '2026-1', 'reportes/reporte_2026_1.pdf', 1);

-- ===============================================================
-- REINICIAR SECUENCIAS (PostgreSQL)
-- ===============================================================
SELECT setval(pg_get_serial_sequence('usuario',        'id_usuario'),        200);
SELECT setval(pg_get_serial_sequence('rol',            'id_rol'),            200);
SELECT setval(pg_get_serial_sequence('permission',     'id_permission'),     200);
SELECT setval(pg_get_serial_sequence('emprendimiento', 'id_emprendimiento'), 200);
SELECT setval(pg_get_serial_sequence('producto',       'id_producto'),       200);
SELECT setval(pg_get_serial_sequence('pedido',         'id_pedido'),         200);
SELECT setval(pg_get_serial_sequence('detalle_pedido', 'id_detalle'),        200);
SELECT setval(pg_get_serial_sequence('categoria',      'id_categoria'),      200);
SELECT setval(pg_get_serial_sequence('estado',         'id_estado'),         200);
SELECT setval(pg_get_serial_sequence('semestre',       'id_semestre'),       200);
SELECT setval(pg_get_serial_sequence('imagen_producto','id_imagen'),         200);
SELECT setval(pg_get_serial_sequence('calificacion',   'id_calificacion'),   200);
SELECT setval(pg_get_serial_sequence('reporte',        'id_reporte'),        200);
SELECT setval(pg_get_serial_sequence('notificacion',   'id_notificacion'),   200);
