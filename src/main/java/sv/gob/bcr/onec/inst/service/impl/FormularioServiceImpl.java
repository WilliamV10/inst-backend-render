package sv.gob.bcr.onec.inst.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import sv.gob.bcr.onec.inst.dto.request.FormularioCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.FormularioUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.FormularioResponse;
import sv.gob.bcr.onec.inst.entity.CodigoAcceso;
import sv.gob.bcr.onec.inst.entity.Formulario;
import sv.gob.bcr.onec.inst.entity.Seccion;
import sv.gob.bcr.onec.inst.exception.ConflictException;
import sv.gob.bcr.onec.inst.exception.NotFoundException;
import sv.gob.bcr.onec.inst.repository.CodigoAccesoRepository;
import sv.gob.bcr.onec.inst.repository.FormularioRepository;
import sv.gob.bcr.onec.inst.repository.SeccionRepository;
import sv.gob.bcr.onec.inst.service.interfaces.FormularioService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FormularioServiceImpl implements FormularioService {

    private final FormularioRepository repository;
    private final SeccionRepository seccionRepository;
    private final CodigoAccesoRepository codigoAccesoRepository;
    private final SecureRandom random = new SecureRandom();

    private FormularioResponse toResponse(Formulario obj) {
        return FormularioResponse.builder()
                .idFormulario(obj.getIdFormulario())
                .codigo(obj.getCodigo())
                .nombre(obj.getNombre())
                .metadata(obj.getMetadata())
                .build();
    }
    private FormularioResponse toResponseSinJson(Formulario obj) {
        
        return FormularioResponse.builder()
                .idFormulario(obj.getIdFormulario())
                .codigo(obj.getCodigo())
                .nombre(obj.getNombre())
                .build();
    }

    @Override
    @Transactional
    public FormularioResponse create(FormularioCreateRequest request) {
        if (repository.existsByCodigo(request.codigo())) {
            throw new ConflictException("Ya existe un formulario con el código: " + request.codigo());
        }

        JsonNode metadata = request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode();

        Formulario entity = Formulario.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .metadata(metadata)
                .build();

        Formulario saved = repository.save(entity);

        // Crear secciones y códigos de acceso a partir del JSON metadata
        crearSeccionesDesdeMetadata(saved, metadata);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FormularioResponse getById(Integer id) {
        Formulario entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formulario not found. id=" + id));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormularioResponse> list() {
        return repository.findAll().stream().map(this::toResponseSinJson).toList();
    }

    @Override
    @Transactional
    public FormularioResponse update(Integer id, FormularioUpdateRequest request) {
        Formulario entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formulario not found. id=" + id));

        if (repository.existsByCodigoAndIdFormularioNot(request.codigo(), id)) {
            throw new ConflictException("Ya existe otro formulario con el código: " + request.codigo());
        }

        JsonNode metadata = request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode();

        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        entity.setMetadata(metadata);
        Formulario saved = repository.save(entity);

        // Sincronizar secciones: crear las nuevas que vengan en el JSON
        sincronizarSecciones(saved, metadata);

        return toResponse(saved);
    }

    

    /**
     * Crea registros de Seccion y CodigoAcceso por cada sección encontrada en metadata.secciones.
     */
    private void crearSeccionesDesdeMetadata(Formulario formulario, JsonNode metadata) {
        JsonNode secciones = metadata.get("secciones");
        if (secciones == null || !secciones.isArray()) {
            return;
        }

        for (JsonNode seccionNode : secciones) {
            String codigoSeccion = obtenerTexto(seccionNode, "codigo");
            String nombreSeccion = obtenerTexto(seccionNode, "nombre");

            if (codigoSeccion == null || nombreSeccion == null) {
                continue;
            }

            // Construir metadata de la sección (todo el nodo excepto codigo/nombre)
            JsonNode metadataSeccion = construirMetadataSeccion(seccionNode);

            Seccion seccion = Seccion.builder()
                    .formulario(formulario)
                    .codigo(codigoSeccion)
                    .nombre(nombreSeccion)
                    .metadata(metadataSeccion)
                    .enEdicion(false)
                    .build();

            seccionRepository.save(seccion);

            
        }
    }

    /**
     * Detecta secciones nuevas en el metadata y las crea junto con su código de acceso.
     * Las secciones existentes (por código) no se duplican.
     */
    private void sincronizarSecciones(Formulario formulario, JsonNode metadata) {
        JsonNode secciones = metadata.get("secciones");
        if (secciones == null || !secciones.isArray()) {
            return;
        }

        // Códigos de secciones que ya existen en BD para este formulario
        Set<String> codigosExistentes = seccionRepository
                .findByFormulario_IdFormulario(formulario.getIdFormulario())
                .stream()
                .map(Seccion::getCodigo)
                .collect(Collectors.toSet());

        for (JsonNode seccionNode : secciones) {
            String codigoSeccion = obtenerTexto(seccionNode, "codigo");
            String nombreSeccion = obtenerTexto(seccionNode, "nombre");

            if (codigoSeccion == null || nombreSeccion == null) {
                continue;
            }

            // Solo crear si es una sección nueva
            if (codigosExistentes.contains(codigoSeccion)) {
                continue;
            }

            JsonNode metadataSeccion = construirMetadataSeccion(seccionNode);

            Seccion seccion = Seccion.builder()
                    .formulario(formulario)
                    .codigo(codigoSeccion)
                    .nombre(nombreSeccion)
                    .metadata(metadataSeccion)
                    .enEdicion(false)
                    .build();

            seccionRepository.save(seccion);

            
        }
    }

    private String obtenerTexto(JsonNode node, String campo) {
        JsonNode valor = node.get(campo);
        return (valor != null && valor.isTextual()) ? valor.asText() : null;
    }

    /**
     * Construye el JSON de metadata de la sección a partir del nodo completo,
     * excluyendo los campos "codigo" y "nombre" que ya se persisten como columnas.
     */
    private JsonNode construirMetadataSeccion(JsonNode seccionNode) {
        ObjectNode meta = JsonNodeFactory.instance.objectNode();
        seccionNode.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            if (!"codigo".equals(key) && !"nombre".equals(key)) {
                meta.set(key, entry.getValue());
            }
        });
        return meta;
    }
}
