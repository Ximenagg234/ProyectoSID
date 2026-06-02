-- =============================================================================
-- SCHEMA COMPLETO — Emprende ICESI en Supabase (PostgreSQL)
-- Pegar y ejecutar en Supabase → SQL Editor → New query
-- Solo contiene las tablas que quedan en PostgreSQL (usuarios, roles, permisos)
-- Las entidades transaccionales (emprendimientos, pedidos, productos) van en MongoDB
-- =============================================================================

-- Limpiar tablas si existen (para reinstalación limpia)
DROP TABLE IF EXISTS auditoria_usuario CASCADE;
DROP TABLE IF EXISTS role_permission CASCADE;
DROP TABLE IF EXISTS user_role CASCADE;
DROP TABLE IF EXISTS permission CASCADE;
DROP TABLE IF EXISTS role CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;

-- =============================================================================
-- TABLA: usuario
-- Fuente de verdad para autenticación y autorización
-- Restricción: solo correos @icesi.edu.co o @u.icesi.edu.co
-- =============================================================================
CREATE TABLE usuario (
    id_usuario          SERIAL PRIMARY KEY,
    nombre_completo     VARCHAR(255) NOT NULL,
    correo_institucional VARCHAR(255) NOT NULL UNIQUE,
    programa_academico  VARCHAR(255),
    semestre_academico  INTEGER,
    foto_perfil         VARCHAR(500),
    clave               VARCHAR(255) NOT NULL,

    CONSTRAINT chk_correo_institucional CHECK (
        LOWER(correo_institucional) LIKE '%@icesi.edu.co'
        OR LOWER(correo_institucional) LIKE '%@u.icesi.edu.co'
    )
);

-- =============================================================================
-- TABLA: role
-- Roles del sistema: ADMIN, EMPRENDEDOR, COMPRADOR
-- =============================================================================
CREATE TABLE role (
    id_rol  SERIAL PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL UNIQUE
);

-- =============================================================================
-- TABLA: permission
-- Permisos granulares por operación
-- =============================================================================
CREATE TABLE permission (
    id_permission  SERIAL PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL UNIQUE,
    descripcion    VARCHAR(255)
);

-- =============================================================================
-- TABLA: user_role (N:M)
-- =============================================================================
CREATE TABLE user_role (
    id_usuario  INTEGER NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    id_rol      INTEGER NOT NULL REFERENCES role(id_rol) ON DELETE CASCADE,
    PRIMARY KEY (id_usuario, id_rol)
);

-- =============================================================================
-- TABLA: role_permission (N:M)
-- =============================================================================
CREATE TABLE role_permission (
    id_rol        INTEGER NOT NULL REFERENCES role(id_rol) ON DELETE CASCADE,
    id_permission INTEGER NOT NULL REFERENCES permission(id_permission) ON DELETE CASCADE,
    PRIMARY KEY (id_rol, id_permission)
);

-- =============================================================================
-- TABLA: auditoria_usuario (trigger automático)
-- =============================================================================
CREATE TABLE auditoria_usuario (
    id              SERIAL PRIMARY KEY,
    id_usuario      INTEGER,
    operacion       VARCHAR(10),
    correo_anterior VARCHAR(255),
    correo_nuevo    VARCHAR(255),
    nombre_anterior VARCHAR(255),
    nombre_nuevo    VARCHAR(255),
    fecha           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_pg      VARCHAR(100) DEFAULT CURRENT_USER
);

-- =============================================================================
-- TRIGGERS
-- =============================================================================

-- Trigger 1: Validación de correo institucional
CREATE OR REPLACE FUNCTION fn_validar_correo_institucional()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.correo_institucional IS NULL OR NEW.correo_institucional = '' THEN
        RAISE EXCEPTION 'El correo institucional es obligatorio.'
            USING ERRCODE = 'check_violation';
    END IF;
    IF LOWER(NEW.correo_institucional) NOT LIKE '%@icesi.edu.co'
       AND LOWER(NEW.correo_institucional) NOT LIKE '%@u.icesi.edu.co' THEN
        RAISE EXCEPTION 'El correo "%" no es válido. Solo se permiten correos @icesi.edu.co o @u.icesi.edu.co.',
            NEW.correo_institucional
            USING ERRCODE = 'check_violation';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_correo
    BEFORE INSERT OR UPDATE OF correo_institucional ON usuario
    FOR EACH ROW EXECUTE FUNCTION fn_validar_correo_institucional();

-- Trigger 2: Auditoría de cambios en usuario
CREATE OR REPLACE FUNCTION fn_auditar_usuario()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO auditoria_usuario(id_usuario, operacion, correo_anterior, nombre_anterior)
        VALUES (OLD.id_usuario, 'DELETE', OLD.correo_institucional, OLD.nombre_completo);
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO auditoria_usuario(id_usuario, operacion,
            correo_anterior, correo_nuevo, nombre_anterior, nombre_nuevo)
        VALUES (NEW.id_usuario, 'UPDATE',
            OLD.correo_institucional, NEW.correo_institucional,
            OLD.nombre_completo, NEW.nombre_completo);
        RETURN NEW;
    ELSIF TG_OP = 'INSERT' THEN
        INSERT INTO auditoria_usuario(id_usuario, operacion, correo_nuevo, nombre_nuevo)
        VALUES (NEW.id_usuario, 'INSERT', NEW.correo_institucional, NEW.nombre_completo);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_usuario
    AFTER INSERT OR UPDATE OR DELETE ON usuario
    FOR EACH ROW EXECUTE FUNCTION fn_auditar_usuario();

-- =============================================================================
-- DATOS INICIALES
-- =============================================================================

-- Roles
INSERT INTO role (nombre) VALUES ('ADMIN');
INSERT INTO role (nombre) VALUES ('EMPRENDEDOR');
INSERT INTO role (nombre) VALUES ('COMPRADOR');

-- Permisos
INSERT INTO permission (nombre, descripcion) VALUES ('CREAR_USUARIO',    'Crear usuarios');
INSERT INTO permission (nombre, descripcion) VALUES ('VER_PRODUCTOS',    'Ver productos');
INSERT INTO permission (nombre, descripcion) VALUES ('CREAR_PRODUCTO',   'Crear productos');
INSERT INTO permission (nombre, descripcion) VALUES ('ELIMINAR_PRODUCTO','Eliminar productos');

-- Role-Permission: ADMIN tiene todos
INSERT INTO role_permission VALUES (1,1),(1,2),(1,3),(1,4);
-- EMPRENDEDOR: ver + crear + eliminar productos
INSERT INTO role_permission VALUES (2,2),(2,3),(2,4);
-- COMPRADOR: solo ver
INSERT INTO role_permission VALUES (3,2);

-- Usuarios (clave: 1234 — BCrypt)
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Ximena Gomez',  'ximena@icesi.edu.co',  'Ingenieria de Sistemas', 6, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Carlos Perez',  'carlos@icesi.edu.co',  'Ingenieria de Sistemas', 5, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Ana Martinez',  'ana@icesi.edu.co',     'Diseno Industrial',      7, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Juan Restrepo', 'juan@icesi.edu.co',    'Administracion',         4, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Sofia Ramirez', 'sofia@icesi.edu.co',   'Gastronomia',            3, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Miguel Torres', 'miguel@icesi.edu.co',  'Ingenieria de Sistemas', 8, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('Laura Sanchez', 'laura@icesi.edu.co',   'Psicologia',             5, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');
INSERT INTO usuario (nombre_completo, correo_institucional, programa_academico, semestre_academico, foto_perfil, clave)
VALUES ('David Gomez',   'david@icesi.edu.co',   'Comunicacion Social',    2, NULL, '$2a$10$uWo5bh2kOry7q9mDhlkhsOLsvdT01EFKfqSIWm3KNm4Hfws4nmwd2');

-- User-Role
INSERT INTO user_role VALUES (1,1),(1,2); -- Ximena: ADMIN + EMPRENDEDOR
INSERT INTO user_role VALUES (2,2);       -- Carlos: EMPRENDEDOR
INSERT INTO user_role VALUES (3,2);       -- Ana: EMPRENDEDOR
INSERT INTO user_role VALUES (4,3);       -- Juan: COMPRADOR
INSERT INTO user_role VALUES (5,2);       -- Sofia: EMPRENDEDOR
INSERT INTO user_role VALUES (6,2),(6,3); -- Miguel: EMPRENDEDOR + COMPRADOR
INSERT INTO user_role VALUES (7,3);       -- Laura: COMPRADOR
INSERT INTO user_role VALUES (8,3);       -- David: COMPRADOR

-- Secuencias
SELECT setval('usuario_id_usuario_seq',    200);
SELECT setval('role_id_rol_seq',           200);
SELECT setval('permission_id_permission_seq', 200);
