-- ============================================================
-- Secuencias para generación de IDs (sin modificar tablas)
-- Ejecutar en la base de datos postgres, schema bd
-- ============================================================

CREATE SEQUENCE IF NOT EXISTS bd.proyecto_id_seq          START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.encuesta_id_seq          START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.encuesta_version_id_seq  START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.grupo_id_seq             START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.seccion_id_seq           START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.pagina_id_seq            START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.tipo_pregunta_id_seq     START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.pregunta_id_seq          START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.tipo_lista_id_seq        START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.lista_id_seq             START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.lista_item_id_seq        START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.tipo_expresion_id_seq    START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS bd.expresion_id_seq         START 1 INCREMENT 1;

-- Ajusta el valor inicial de cada secuencia al máximo ID existente:
SELECT setval('bd.proyecto_id_seq',         (SELECT COALESCE(MAX(id_proyecto),        0) + 1 FROM bd.proyecto),        false);
SELECT setval('bd.encuesta_id_seq',          (SELECT COALESCE(MAX(id_encuesta),         0) + 1 FROM bd.encuesta),         false);
SELECT setval('bd.encuesta_version_id_seq',  (SELECT COALESCE(MAX(id_encuesta_version), 0) + 1 FROM bd.encuesta_version),  false);
SELECT setval('bd.grupo_id_seq',             (SELECT COALESCE(MAX(id_grupo),            0) + 1 FROM bd.grupo),             false);
SELECT setval('bd.seccion_id_seq',           (SELECT COALESCE(MAX(id_seccion),          0) + 1 FROM bd.seccion),           false);
SELECT setval('bd.pagina_id_seq',            (SELECT COALESCE(MAX(id_pagina),           0) + 1 FROM bd.pagina),            false);
SELECT setval('bd.tipo_pregunta_id_seq',     (SELECT COALESCE(MAX(id_tipo_pregunta),    0) + 1 FROM bd.tipo_pregunta),     false);
SELECT setval('bd.pregunta_id_seq',          (SELECT COALESCE(MAX(id_pregunta),         0) + 1 FROM bd.pregunta),          false);
SELECT setval('bd.tipo_lista_id_seq',        (SELECT COALESCE(MAX(id_tipo_lista),       0) + 1 FROM bd.tipo_lista),        false);
SELECT setval('bd.lista_id_seq',             (SELECT COALESCE(MAX(id_lista),            0) + 1 FROM bd.lista),             false);
SELECT setval('bd.lista_item_id_seq',        (SELECT COALESCE(MAX(id_lista_item),       0) + 1 FROM bd.lista_item),        false);
SELECT setval('bd.tipo_expresion_id_seq',    (SELECT COALESCE(MAX(id_tipo_expresion),   0) + 1 FROM bd.tipo_expresion),    false);
SELECT setval('bd.expresion_id_seq',         (SELECT COALESCE(MAX(id_expresion),        0) + 1 FROM bd.expresion),         false);

-- ============================================================
-- Triggers: asignan el ID desde la secuencia si viene NULL
-- Útiles para inserts directos por SQL o aplicaciones externas
-- ============================================================

CREATE OR REPLACE FUNCTION bd.trg_set_proyecto_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_proyecto IS NULL THEN
        NEW.id_proyecto := nextval('bd.proyecto_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_proyecto_id
    BEFORE INSERT ON bd.proyecto
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_proyecto_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_encuesta_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_encuesta IS NULL THEN
        NEW.id_encuesta := nextval('bd.encuesta_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_encuesta_id
    BEFORE INSERT ON bd.encuesta
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_encuesta_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_encuesta_version_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_encuesta_version IS NULL THEN
        NEW.id_encuesta_version := nextval('bd.encuesta_version_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_encuesta_version_id
    BEFORE INSERT ON bd.encuesta_version
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_encuesta_version_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_grupo_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_grupo IS NULL THEN
        NEW.id_grupo := nextval('bd.grupo_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_grupo_id
    BEFORE INSERT ON bd.grupo
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_grupo_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_seccion_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_seccion IS NULL THEN
        NEW.id_seccion := nextval('bd.seccion_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_seccion_id
    BEFORE INSERT ON bd.seccion
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_seccion_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_pagina_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_pagina IS NULL THEN
        NEW.id_pagina := nextval('bd.pagina_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_pagina_id
    BEFORE INSERT ON bd.pagina
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_pagina_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_tipo_pregunta_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_tipo_pregunta IS NULL THEN
        NEW.id_tipo_pregunta := nextval('bd.tipo_pregunta_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_tipo_pregunta_id
    BEFORE INSERT ON bd.tipo_pregunta
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_tipo_pregunta_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_pregunta_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_pregunta IS NULL THEN
        NEW.id_pregunta := nextval('bd.pregunta_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_pregunta_id
    BEFORE INSERT ON bd.pregunta
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_pregunta_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_tipo_lista_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_tipo_lista IS NULL THEN
        NEW.id_tipo_lista := nextval('bd.tipo_lista_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_tipo_lista_id
    BEFORE INSERT ON bd.tipo_lista
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_tipo_lista_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_lista_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_lista IS NULL THEN
        NEW.id_lista := nextval('bd.lista_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_lista_id
    BEFORE INSERT ON bd.lista
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_lista_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_lista_item_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_lista_item IS NULL THEN
        NEW.id_lista_item := nextval('bd.lista_item_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_lista_item_id
    BEFORE INSERT ON bd.lista_item
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_lista_item_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_tipo_expresion_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_tipo_expresion IS NULL THEN
        NEW.id_tipo_expresion := nextval('bd.tipo_expresion_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_tipo_expresion_id
    BEFORE INSERT ON bd.tipo_expresion
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_tipo_expresion_id();

-- ---

CREATE OR REPLACE FUNCTION bd.trg_set_expresion_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_expresion IS NULL THEN
        NEW.id_expresion := nextval('bd.expresion_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_expresion_id
    BEFORE INSERT ON bd.expresion
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_expresion_id();

-- tmp_diccionario
CREATE SEQUENCE IF NOT EXISTS bd.tmp_diccionario_id_seq  START 1 INCREMENT 1;
SELECT setval('bd.tmp_diccionario_id_seq', (SELECT COALESCE(MAX(id_diccionario), 0) + 1 FROM bd.tmp_diccionario), false);

CREATE OR REPLACE FUNCTION bd.trg_set_tmp_diccionario_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_diccionario IS NULL THEN
        NEW.id_diccionario := nextval('bd.tmp_diccionario_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_tmp_diccionario_id
    BEFORE INSERT ON bd.tmp_diccionario
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_tmp_diccionario_id();

-- pregunta_grupo
CREATE SEQUENCE IF NOT EXISTS bd.pregunta_grupo_id_seq  START 1 INCREMENT 1;
SELECT setval('bd.pregunta_grupo_id_seq', (SELECT COALESCE(MAX(id_pregunta_grupo), 0) + 1 FROM bd.pregunta_grupo), false);

CREATE OR REPLACE FUNCTION bd.trg_set_pregunta_grupo_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.id_pregunta_grupo IS NULL THEN
        NEW.id_pregunta_grupo := nextval('bd.pregunta_grupo_id_seq');
    END IF;
    RETURN NEW;
END; $$;
CREATE OR REPLACE TRIGGER trg_pregunta_grupo_id
    BEFORE INSERT ON bd.pregunta_grupo
    FOR EACH ROW EXECUTE FUNCTION bd.trg_set_pregunta_grupo_id();
