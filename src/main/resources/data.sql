-- ESTADO
INSERT INTO estado (id_estado, nombre) VALUES (1, 'ACTIVO');
INSERT INTO estado (id_estado, nombre) VALUES (2, 'INACTIVO');

-- CATEGORIA
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (1, 'Tecnologia', 'Productos tecnologicos');
INSERT INTO categoria (id_categoria, nombre, descripcion) VALUES (2, 'Moda', 'Ropa y accesorios');

-- SEMESTRE
INSERT INTO semestre (id_semestre, periodo, fecha_inicio, fecha_fin, id_estado)
VALUES (1, '2026-1', CURRENT_DATE, CURRENT_DATE, 1);

-- ROLES
INSERT INTO rol (id_rol, nombre) VALUES (1, 'ADMIN');
INSERT INTO rol (id_rol, nombre) VALUES (2, 'EMPRENDEDOR');
INSERT INTO rol (id_rol, nombre) VALUES (3, 'CLIENTE');

-- PERMISSIONS
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (1, 'CREAR_USUARIO', 'Crear usuarios');
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (2, 'VER_PRODUCTOS', 'Ver productos');
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (3, 'CREAR_PRODUCTO', 'Crear productos');
INSERT INTO permission (id_permission, nombre, descripcion) VALUES (4, 'ELIMINAR_PRODUCTO', 'Eliminar productos');

-- ROLE_PERMISSION
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 1);
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 2);
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 3);
INSERT INTO role_permission (id_rol, id_permission) VALUES (1, 4);
INSERT INTO role_permission (id_rol, id_permission) VALUES (2, 2);
INSERT INTO role_permission (id_rol, id_permission) VALUES (2, 3);
INSERT INTO role_permission (id_rol, id_permission) VALUES (2, 4);
INSERT INTO role_permission (id_rol, id_permission) VALUES (3, 2);

-- USUARIO (clave: 1234 con BCrypt)
INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (1, 'Ximena Gomez', 'ximena@icesi.edu.co', 'Ingenieria de Sistemas', 6, NULL, '$2a$10$cn2bTff2y4yRtbLPm0gz/.zzNsYvQZp5mXylwRm9Xou.npS2DGS.W');

INSERT INTO usuario (id_usuario, nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES (2, 'Carlos Perez', 'carlos@icesi.edu.co', 'Ingenieria de Sistemas', 5, NULL, '$2a$10$gDMDYOC7MQ5Abz8wG1v3FeNnZ8Bo2Ockgkegxw2eYoyZiXjf6Ahf2');

-- USER_ROLE
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 1);
INSERT INTO user_role (id_usuario, id_rol) VALUES (1, 2);
INSERT INTO user_role (id_usuario, id_rol) VALUES (2, 3);

-- EMPRENDIMIENTO
INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (1, 'TechStore', 'Venta de tecnologia', NULL, 1, 1, 1, 1);

INSERT INTO emprendimiento (id_emprendimiento, nombre, descripcion, logo_url, id_usuario, id_categoria, id_semestre, id_estado)
VALUES (2, 'ModaCool', 'Ropa moderna', NULL, 2, 2, 1, 1);

-- PRODUCTO
INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (1, 'iPhone 13', 'Celular Apple', 3000000, 10, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (2, 'AirPods', 'Audifonos Apple', 800000, 15, 1, 1);

INSERT INTO producto (id_producto, nombre, descripcion, precio, stock_disponible, id_emprendimiento, id_estado)
VALUES (3, 'Camiseta Oversize', 'Ropa urbana', 50000, 30, 2, 1);

-- IMAGEN_PRODUCTO
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (1, 'img1.jpg', 1);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (2, 'img2.jpg', 1);
INSERT INTO imagen_producto (id_imagen, url_imagen, id_producto) VALUES (3, 'img3.jpg', 2);

-- PEDIDO
INSERT INTO pedido (id_pedido, fecha_pedido, total, id_usuario, id_emprendimiento, id_estado)
VALUES (1, CURRENT_TIMESTAMP, 3800000, 2, 1, 1);

-- DETALLE_PEDIDO
INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (1, 1, 3000000, 3000000, 1, 1);

INSERT INTO detalle_pedido (id_detalle, cantidad, precio_unitario, subtotal, id_pedido, id_producto)
VALUES (2, 1, 800000, 800000, 1, 2);

-- CALIFICACION
INSERT INTO calificacion (id_calificacion, puntuacion, comentario, fecha, id_usuario, id_emprendimiento, id_pedido)
VALUES (1, 5, 'Excelente servicio', CURRENT_TIMESTAMP, 2, 1, 1);

-- NOTIFICACION
INSERT INTO notificacion (id_notificacion, tipo, contenido, fecha, id_usuario, referencia_id)
VALUES (1, 'PEDIDO', 'Nuevo pedido recibido', CURRENT_TIMESTAMP, 1, 1);

-- MENSAJE
INSERT INTO mensaje (id_mensaje, contenido, fecha, id_remitente, id_receptor, id_pedido)
VALUES (1, 'Tu pedido va en camino', CURRENT_TIMESTAMP, 1, 2, 1);

-- REPORTE
INSERT INTO reporte (id_reporte, fecha_generacion, periodo, ruta_archivo, id_usuario)
VALUES (1, CURRENT_TIMESTAMP, '2026-1', 'reporte.pdf', 1);

ALTER TABLE usuario ALTER COLUMN id_usuario RESTART WITH 100;
ALTER TABLE rol ALTER COLUMN id_rol RESTART WITH 100;
ALTER TABLE permission ALTER COLUMN id_permission RESTART WITH 100;
ALTER TABLE emprendimiento ALTER COLUMN id_emprendimiento RESTART WITH 100;
ALTER TABLE producto ALTER COLUMN id_producto RESTART WITH 100;
ALTER TABLE pedido ALTER COLUMN id_pedido RESTART WITH 100;
ALTER TABLE detalle_pedido ALTER COLUMN id_detalle RESTART WITH 100;
ALTER TABLE calificacion ALTER COLUMN id_calificacion RESTART WITH 100;
ALTER TABLE notificacion ALTER COLUMN id_notificacion RESTART WITH 100;
ALTER TABLE mensaje ALTER COLUMN id_mensaje RESTART WITH 100;
ALTER TABLE reporte ALTER COLUMN id_reporte RESTART WITH 100;
ALTER TABLE imagen_producto ALTER COLUMN id_imagen RESTART WITH 100;
ALTER TABLE semestre ALTER COLUMN id_semestre RESTART WITH 100;
ALTER TABLE categoria ALTER COLUMN id_categoria RESTART WITH 100;
ALTER TABLE estado ALTER COLUMN id_estado RESTART WITH 100;