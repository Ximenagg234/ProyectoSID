-- =============================================================================
-- TRIGGERS POSTGRESQL — Emprende ICESI
-- Ejecutar en Supabase SQL Editor (o pgAdmin) una sola vez
-- =============================================================================

-- =============================================================================
-- TRIGGER 1: Validación de correo institucional
-- Garantiza que solo se registren correos @icesi.edu.co o @u.icesi.edu.co
-- Se dispara en INSERT y UPDATE sobre la tabla app_usuario (o usuario)
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_validar_correo_institucional()
RETURNS TRIGGER AS $$
BEGIN
    -- Verificar que el correo tenga dominio institucional
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

-- Crear el trigger (DROP primero por si ya existe)
DROP TRIGGER IF EXISTS trg_validar_correo ON usuario;

CREATE TRIGGER trg_validar_correo
    BEFORE INSERT OR UPDATE OF correo_institucional ON usuario
    FOR EACH ROW
    EXECUTE FUNCTION fn_validar_correo_institucional();

-- =============================================================================
-- TRIGGER 2: Auditoría de cambios en usuarios
-- Registra en una tabla de auditoría quién, cuándo y qué cambió
-- =============================================================================

CREATE TABLE IF NOT EXISTS auditoria_usuario (
    id              SERIAL PRIMARY KEY,
    id_usuario      INTEGER,
    operacion       VARCHAR(10),   -- INSERT | UPDATE | DELETE
    correo_anterior VARCHAR(255),
    correo_nuevo    VARCHAR(255),
    nombre_anterior VARCHAR(255),
    nombre_nuevo    VARCHAR(255),
    fecha           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_pg      VARCHAR(100) DEFAULT CURRENT_USER
);

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

DROP TRIGGER IF EXISTS trg_auditoria_usuario ON usuario;

CREATE TRIGGER trg_auditoria_usuario
    AFTER INSERT OR UPDATE OR DELETE ON usuario
    FOR EACH ROW
    EXECUTE FUNCTION fn_auditar_usuario();

-- =============================================================================
-- TRIGGER 3: Prevenir eliminación de usuario con emprendimientos activos
-- (solo aplica si emprendimientos también estuvieran en PostgreSQL,
--  pero documenta la intención de integridad referencial)
-- =============================================================================

-- En este diseño, emprendimientos están en MongoDB.
-- La integridad se garantiza a nivel de aplicación en MongoSyncService.
-- Este comentario documenta la decisión de arquitectura.

-- =============================================================================
-- CONSTRAINT ADICIONAL: CHECK de correo como segunda capa (nivel columna)
-- =============================================================================

ALTER TABLE usuario
    DROP CONSTRAINT IF EXISTS chk_correo_institucional;

ALTER TABLE usuario
    ADD CONSTRAINT chk_correo_institucional
    CHECK (
        LOWER(correo_institucional) LIKE '%@icesi.edu.co'
        OR LOWER(correo_institucional) LIKE '%@u.icesi.edu.co'
    );

-- =============================================================================
-- Verificar que los triggers quedaron creados
-- =============================================================================
SELECT trigger_name, event_manipulation, event_object_table, action_timing
FROM information_schema.triggers
WHERE trigger_schema = 'public'
ORDER BY trigger_name;
