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

-- =========================
-- USUARIOS (clave: 1234)
-- =========================
INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (1, 'Ximena Gomez', 'ximena@icesi.edu.co', 'Ingenieria de Sistemas', 6, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (2, 'Carlos Perez', 'carlos@icesi.edu.co', 'Ingenieria de Sistemas', 5, NULL, '$2a$10$wFQ63ItOxgsq3rH.u2I13eQj/Df66cPrlJT02KmQHgiwD.yX09zUS');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (3, 'Ana Martinez', 'ana@icesi.edu.co', 'Diseño Industrial', 7, NULL, '$2a$10$oT2W2PkCmmTol0Y.WD9KjOXUrxO6svMrLQGSYLfSeJyHK04bmDiqW');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (4, 'Juan Restrepo', 'juan@icesi.edu.co', 'Administracion de Empresas', 4, NULL, '$2a$10$VPrIuN19hD/3.Fmlg7M.ZO/WE.qzFzdPt/xFIymtDJl.9vtDyzaAa');

-- =========================
-- USER_ROLE
-- =========================
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 1);
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (2, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (3, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (4, 3);

-- =========================
-- EMPRENDIMIENTOS
-- =========================
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (1, 'TechStore', 'Accesorios y gadgets tecnologicos para estudiantes', NULL, 1, 1, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (2, 'IcesiWear', 'Ropa urbana y accesorios de moda universitaria', NULL, 2, 2, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (3, 'CafeIcesi', 'Cafe de especialidad preparado por estudiantes de Icesi', NULL, 3, 4, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (4, 'ArteU', 'Ilustraciones y productos artisticos personalizados', NULL, 3, 5, 1, 1);

-- =========================
-- PRODUCTOS
-- =========================
-- TechStore (id=1, categoria: Tecnologia)
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (1, 'iPhone 13', 'Celular Apple reacondicionado en perfecto estado', 3000000, 5, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (2, 'AirPods Pro', 'Audifonos Apple originales con cancelacion de ruido', 800000, 8, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (3, 'Cargador USB-C 65W', 'Cargador rapido compatible con laptops y celulares', 45000, 20, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (4, 'Mouse Logitech M185', 'Mouse inalambrico ergonomico para trabajo y estudio', 55000, 12, 1, 1);

-- IcesiWear (id=2, categoria: Moda)
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (5, 'Camiseta Oversize', 'Camiseta oversize con bordado exclusivo Icesi', 50000, 30, 2, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (6, 'Tote Bag Canvas', 'Bolsa de tela con estampado universitario', 35000, 25, 2, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (7, 'Hoodie Universitario', 'Sudadera con capucha estilo universitario premium', 95000, 15, 2, 1);

-- CafeIcesi (id=3, categoria: Bebidas)
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (8, 'Cappuccino Especial', 'Cappuccino con leche de avena y espresso doble', 8000, 50, 3, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (9, 'Latte Caramelo', 'Latte caliente con sirope artesanal de caramelo', 9000, 40, 3, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (10, 'Pack Brownies x6', 'Brownies artesanales de chocolate belga', 28000, 20, 3, 1);

-- ArteU (id=4, categoria: Arte)
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (11, 'Sticker Pack x10', 'Set de stickers ilustrados con tematica universitaria', 12000, 50, 4, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (12, 'Ilustracion Personalizada', 'Retrato digital en estilo caricatura, entrega en 3 dias', 45000, 10, 4, 1);

-- =========================
-- PEDIDO de prueba
-- =========================
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (1, CURRENT_TIMESTAMP, 58000, 4, 2, 4);

INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (1, 1, 50000, 50000, 1, 5);

INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (2, 1, 8000, 8000, 1, 8);

-- =========================
-- IMAGEN_PRODUCTO
-- =========================
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (1, 'img1.jpg', 1);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (2, 'img2.jpg', 8);

-- =========================
-- NOTIFICACION
-- =========================
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (1, 'PEDIDO', 'Nuevo pedido recibido en IcesiWear', CURRENT_TIMESTAMP, 2, 1);

-- =========================
-- REPORTE
-- =========================
INSERT INTO reporte (id_reporte, fecha_generacion, periodo, ruta_archivo, id_usuario)
VALUES (1, CURRENT_TIMESTAMP, '2026-1', 'reporte.pdf', 1);

-- =========================
-- RESTART SEQUENCES
-- =========================
ALTER TABLE usuario ALTER COLUMN id_usuario RESTART WITH 100;
ALTER TABLE rol ALTER COLUMN id_rol RESTART WITH 100;
ALTER TABLE permission ALTER COLUMN id_permission RESTART WITH 100;
ALTER TABLE emprendimiento ALTER COLUMN id_emprendimiento RESTART WITH 100;
ALTER TABLE producto ALTER COLUMN id_producto RESTART WITH 100;
ALTER TABLE pedido ALTER COLUMN id_pedido RESTART WITH 100;
ALTER TABLE detalle_pedido ALTER COLUMN id_detalle RESTART WITH 100;
ALTER TABLE categoria ALTER COLUMN id_categoria RESTART WITH 100;
ALTER TABLE estado ALTER COLUMN id_estado RESTART WITH 100;
ALTER TABLE semestre ALTER COLUMN id_semestre RESTART WITH 100;
ALTER TABLE imagen_producto ALTER COLUMN id_imagen RESTART WITH 100;
ALTER TABLE reporte ALTER COLUMN id_reporte RESTART WITH 100;
ALTER TABLE notificacion ALTER COLUMN id_notificacion RESTART WITH 100;
