# Bitácora de Cambios — sie-encuestas

## 2026-03-04

---

### 1. Configuración de base de datos — `.env`

**Problema:** La aplicación no conectaba a la BD.
**Cambio:** Se configuraron las variables de entorno de conexión:

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=postgres
DB_SCHEMA=bd
DB_USERNAME=postgres
DB_PASSWORD=123
PORT=8082
SERVER_SERVLET_CONTEXT_PATH=/encuestas
```

> Las variables del `.env` no se cargan automáticamente por Spring Boot.
> Deben configurarse en IntelliJ: **Run Configuration → Environment variables**.

---

### 2. Desactivar DDL automático — `application.yaml`

**Problema:** Hibernate intentaba modificar el esquema existente.
**Cambio:** `spring.jpa.hibernate.ddl-auto` de `update` a `none`.

```yaml
# Antes
ddl-auto: update

# Después
ddl-auto: none
```

---

### 3. Secuencias e IDs automáticos — `create-sequences.sql` (archivo nuevo)

**Problema:** Las tablas existentes no tenían `IDENTITY` ni secuencias en sus PKs,
causando error `null value in column "id_*"` al insertar.
**Solución:** Se creó el script `create-sequences.sql` con:

- 13 `CREATE SEQUENCE IF NOT EXISTS` (una por tabla)
- 13 `SELECT setval(...)` para iniciar las secuencias desde el máximo ID existente
- 13 funciones trigger + triggers `BEFORE INSERT` que asignan el siguiente valor de la secuencia si el ID viene nulo

**Tablas cubiertas:** `proyecto`, `encuesta`, `encuesta_version`, `grupo`, `seccion`,
`pagina`, `tipo_pregunta`, `pregunta`, `tipo_lista`, `lista`, `lista_item`,
`tipo_expresion`, `expresion`.

> El script no modifica tablas ni columnas existentes.

---

### 4. Cambio de estrategia de generación de IDs — 13 entidades

**Problema:** `GenerationType.IDENTITY` no funciona con triggers de secuencia en Hibernate 6 / PostgreSQL.
**Cambio:** Se reemplazó en todas las entidades:

```java
// Antes
@GeneratedValue(strategy = GenerationType.IDENTITY)

// Después
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "xxx_gen")
@SequenceGenerator(name = "xxx_gen", sequenceName = "bd.xxx_id_seq", allocationSize = 1)
```

**Entidades modificadas:**
- `Proyecto.java` — secuencia `bd.proyecto_id_seq`
- `Encuesta.java` — secuencia `bd.encuesta_id_seq`
- `EncuestaVersion.java` — secuencia `bd.encuesta_version_id_seq`
- `Grupo.java` — secuencia `bd.grupo_id_seq`
- `Seccion.java` — secuencia `bd.seccion_id_seq`
- `Pagina.java` — secuencia `bd.pagina_id_seq`
- `TipoPregunta.java` — secuencia `bd.tipo_pregunta_id_seq`
- `Pregunta.java` — secuencia `bd.pregunta_id_seq`
- `TipoLista.java` — secuencia `bd.tipo_lista_id_seq`
- `Lista.java` — secuencia `bd.lista_id_seq`
- `ListaItem.java` — secuencia `bd.lista_item_id_seq`
- `TipoExpresion.java` — secuencia `bd.tipo_expresion_id_seq`
- `Expresion.java` — secuencia `bd.expresion_id_seq`

---

### 5. Nombre explícito en `@PathVariable` — 13 controllers

**Problema:** Spring Boot 3 / Spring Framework 6 requiere nombre explícito en `@PathVariable`
o el flag `-parameters` en el compilador. Sin esto retornaba error 400.
**Cambio:** Se agregó el nombre en todos los controllers:

```java
// Antes
public ResponseEntity<...> getById(@PathVariable Long id)

// Después
public ResponseEntity<...> getById(@PathVariable("id") Long id)
```

**Controllers modificados:** `ProyectoController`, `EncuestaController`,
`EncuestaVersionController`, `GrupoController`, `SeccionController`,
`PaginaController`, `TipoPreguntaController`, `PreguntaController`,
`TipoListaController`, `ListaController`, `ListaItemController`,
`TipoExpresionController`, `ExpresionController`.

---

### 6. Flag `-parameters` en compilador — `pom.xml`

**Problema:** Complemento al punto 5 para que Spring resuelva nombres de parámetros en tiempo de compilación.
**Cambio:** Se agregó `<parameters>true</parameters>` al `maven-compiler-plugin`:

```xml
<configuration>
    <source>${java.version}</source>
    <target>${java.version}</target>
    <parameters>true</parameters>
    ...
</configuration>
```

---

### 7. Colección Postman — `sie-encuestas.postman_collection.json` (archivo nuevo)

**Cambio:** Se creó la colección Postman con CRUD completo para las 13 entidades.

**Características:**
- Variable de colección `baseUrl = http://localhost:8082/encuestas/api/v1`
- Cada request POST captura el ID retornado en una variable de colección
  mediante script en la pestaña **Tests**:
  ```javascript
  var r = pm.response.json();
  pm.collectionVariables.set('idProyecto', r.idProyecto);
  ```
- Los requests siguientes usan `{{idProyecto}}`, `{{idEncuesta}}`, etc.
- Los requests deben ejecutarse **en orden** ya que cada entidad depende de la anterior.

**Variables de colección:** `idProyecto`, `idEncuesta`, `idEncuestaVersion`,
`idTipoPreguntaTexto`, `idTipoPreguntaLista`, `idTipoLista`, `idLista`,
`idListaItem`, `idSeccion`, `idPagina`, `idGrupo`, `idPregunta`,
`idTipoExpresion`, `idExpresion`.

> Si se reimporta la colección, las variables se resetean.
> Usar **Listar** (GET all) para obtener IDs existentes y setearlos manualmente
> en **Collection → Variables → Current value**.

---

### 8. Soporte nativo de `jsonb` — `Grupo.java`, `Pregunta.java`, `Expresion.java`

**Problema:** `JsonNodeConverter` convertía `JsonNode` a `String` (varchar),
pero PostgreSQL no permite insertar varchar en columnas `jsonb` sin cast explícito.
Causaba error 500: *"la columna es de tipo jsonb pero la expresión es de tipo character varying"*.

**Cambio:** Se reemplazó `@Convert(converter = JsonNodeConverter.class)` por
`@JdbcTypeCode(SqlTypes.JSON)` de Hibernate 6:

```java
// Antes
@Convert(converter = JsonNodeConverter.class)
@Column(columnDefinition = "jsonb")
private JsonNode metadata;

// Después
@JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "jsonb")
private JsonNode metadata;
```

**Campos afectados:**
- `Grupo.java` → campo `metadata`
- `Pregunta.java` → campo `metadata`
- `Expresion.java` → campo `parametrosJson`

## 2026-03-06

---

### 9. Nueva entidad `tmp_diccionario` — implementación completa

**Tabla:** `bd.tmp_diccionario` (ya existente en BD)
```sql
CREATE TABLE bd.tmp_diccionario (
    id_diccionario int8 NOT NULL,
    data jsonb NOT NULL,
    CONSTRAINT id_diccionario PRIMARY KEY (id_diccionario)
);
```

**Archivos creados:**

| Archivo | Descripción |
|---------|-------------|
| `entity/TmpDiccionario.java` | Entidad JPA con `@JdbcTypeCode(SqlTypes.JSON)` para campo `data` (jsonb) |
| `repository/TmpDiccionarioRepository.java` | `JpaRepository<TmpDiccionario, Long>` |
| `dto/request/TmpDiccionarioCreateRequest.java` | Record con campo `@NotNull JsonNode data` |
| `dto/request/TmpDiccionarioUpdateRequest.java` | Record con campo `@NotNull JsonNode data` |
| `dto/response/TmpDiccionarioResponse.java` | Response con `idDiccionario` y `data` |
| `service/interfaces/TmpDiccionarioService.java` | Interface CRUD |
| `service/impl/TmpDiccionarioServiceImpl.java` | Implementación CRUD |
| `controller/api/TmpDiccionarioApi.java` | Interface OpenAPI con anotaciones Swagger |
| `controller/TmpDiccionarioController.java` | `@RestController` en `/api/v1/dictionary` |

**`create-sequences.sql` actualizado:**
- `CREATE SEQUENCE IF NOT EXISTS bd.tmp_diccionario_id_seq`
- `SELECT setval(...)` desde MAX existente
- Función trigger + trigger `BEFORE INSERT`

**Estrategia de ID:** `GenerationType.SEQUENCE` con `bd.tmp_diccionario_id_seq` (igual al resto de entidades).

> Ejecutar el bloque `-- tmp_diccionario` agregado al final de `create-sequences.sql` en la BD antes de arrancar la app.

---

### 10. Error 500 al crear `tmp_diccionario` — secuencia no creada en BD

**Problema:** POST `/api/v1/dictionary` retorna 500 Internal Server Error.
**Causa probable:** La secuencia `bd.tmp_diccionario_id_seq` no existe en la BD porque el bloque nuevo de `create-sequences.sql` no fue ejecutado.

**Solución:** Ejecutar en la BD el siguiente script:

```sql
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
```

> Verificar existencia de la secuencia:
> ```sql
> SELECT * FROM pg_sequences WHERE schemaname = 'bd' AND sequencename = 'tmp_diccionario_id_seq';
> ```

**Estado:** Pendiente confirmación del stack trace en IntelliJ.

## 2026-03-06 — Sincronización con esquema BD (esquema_bd_06_03_2026.sql)

---

### 11. Campos `metadata` agregados en 5 entidades

**Cambio:** Se agregó `@JdbcTypeCode(SqlTypes.JSON)` + `@Column(columnDefinition = "jsonb")` en:

| Entidad | Campo |
|---------|-------|
| `Proyecto` | `metadata` |
| `Encuesta` | `metadata` |
| `EncuestaVersion` | `metadata` |
| `Seccion` | `metadata` |
| `Pagina` | `metadata` |

**También actualizados:** DTOs (Create/Update Request) y Responses de cada entidad, y sus ServiceImpl (`toResponse`, `create`, `update`).

---

### 12. Campo `origen` agregado en `Encuesta`

**Cambio:** Campo `origen text NOT NULL DEFAULT 'SIE'` faltaba en la entidad.

- `Encuesta.java`: campo `@Column(nullable = false, length = 50) String origen`
- `EncuestaCreateRequest` / `EncuestaUpdateRequest`: campo `String origen` (opcional, default `"SIE"` si null en create)
- `EncuestaResponse`: campo `String origen`
- `EncuestaServiceImpl`: `origen(request.origen() != null ? request.origen() : "SIE")`

---

### 13. Corrección nullability en `Lista`

**Problema:** Java tenía `optional = false` en `encuestaVersion` y `tipoLista`, y `nullable = false` en `nombre`, pero el SQL los define como nullable.

**Cambio en `Lista.java`:**
- `encuestaVersion`: `optional = false` → `optional` omitido (nullable)
- `tipoLista`: `optional = false` → `optional` omitido (nullable)
- `nombre`: `nullable = false` eliminado

---

### 14. Nueva entidad `PreguntaGrupo` — implementación completa

**Tabla:** `bd.pregunta_grupo` (ya existente en BD)

**Archivos creados:**

| Archivo | Ruta |
|---------|------|
| `PreguntaGrupo.java` | `entity/` |
| `PreguntaGrupoRepository.java` | `repository/` |
| `PreguntaGrupoCreateRequest.java` | `dto/request/` |
| `PreguntaGrupoUpdateRequest.java` | `dto/request/` |
| `PreguntaGrupoResponse.java` | `dto/response/` |
| `PreguntaGrupoService.java` | `service/interfaces/` |
| `PreguntaGrupoServiceImpl.java` | `service/impl/` |
| `PreguntaGrupoApi.java` | `controller/api/` |
| `PreguntaGrupoController.java` | `controller/` |

**Endpoint:** `/api/v1/question-groups`

**`create-sequences.sql`:** agregada secuencia `bd.pregunta_grupo_id_seq` + trigger.

> Ejecutar bloque `-- pregunta_grupo` en la BD antes de usar el endpoint.

---

## Cierre de sesión — 2026-03-06 (viernes)

### Estado del proyecto al finalizar la jornada

#### Pendientes para el lunes 2026-03-10

1. **Ejecutar script SQL en BD** — bloqueante para que arranque la app:
   - Bloque `-- tmp_diccionario` de `create-sequences.sql` (secuencia + trigger)
   - Bloque `-- pregunta_grupo` de `create-sequences.sql` (secuencia + trigger)

2. **Verificar error 500 `tmp_diccionario`** — confirmar que se resuelve tras ejecutar el script SQL.
   - Ver logs en IntelliJ para descartar otro problema.

3. **Probar colección Postman** `tmp-diccionario.postman_collection.json` — ejecutar requests en orden.

4. **Probar todos los endpoints afectados** por los cambios del día:
   - Crear/actualizar `Proyecto` con campo `metadata`
   - Crear/actualizar `Encuesta` con campos `origen` y `metadata`
   - Crear/actualizar `EncuestaVersion`, `Seccion`, `Pagina` con `metadata`
   - CRUD completo de `PreguntaGrupo` (`/api/v1/question-groups`)

#### Resumen de archivos tocados hoy (2026-03-06)

| Tipo | Cantidad |
|------|----------|
| Entidades nuevas | 2 (`TmpDiccionario`, `PreguntaGrupo`) |
| Entidades modificadas | 6 |
| DTOs / Responses nuevos | 5 |
| DTOs / Responses modificados | 15 |
| Services nuevos | 2 |
| Services modificados | 5 |
| Controllers / APIs nuevos | 4 |
| Archivos SQL modificados | 1 (`create-sequences.sql`) |
| Colección Postman nueva | 1 (`tmp-diccionario.postman_collection.json`) |
| **Total archivos** | **~40** |
