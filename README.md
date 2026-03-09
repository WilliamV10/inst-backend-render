# Encuestas Backend (Spring Boot)

Arquitectura tradicional por capas:
- controller
- service (interfaces + impl)
- repository
- entity
- dto (request/response)
- config
- exception
- converter (JSON)

## Convenciones solicitadas
- Métodos en inglés: create/getById/list/update/delete
- DTO Response como clases con Lombok (@Builder); request como `record`
- Sin patrón Mapper: armado de Response con builder() dentro de los servicios
- Interfaces en `service/interfaces`
- JSON como `JsonNode` (con JPA AttributeConverter a jsonb)

## Swagger UI
- http://localhost:8080/swagger-ui/index.html
