package sv.gob.bcr.onec.inst.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.bcr.onec.inst.dto.request.SeccionCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionSaveRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.SeccionResponse;
import sv.gob.bcr.onec.inst.entity.Formulario;
import sv.gob.bcr.onec.inst.entity.Seccion;
import sv.gob.bcr.onec.inst.exception.ConflictException;
import sv.gob.bcr.onec.inst.exception.NotFoundException;
import sv.gob.bcr.onec.inst.repository.FormularioRepository;
import sv.gob.bcr.onec.inst.repository.SeccionRepository;
import sv.gob.bcr.onec.inst.service.interfaces.SeccionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeccionServiceImpl implements SeccionService {

    private final SeccionRepository seccionRepository;
    private final FormularioRepository formularioRepository;

    private SeccionResponse toResponse(Seccion obj) {
        return SeccionResponse.builder()
                .idSeccion(obj.getIdSeccion())
                .codigo(obj.getCodigo())
                .idFormulario(obj.getFormulario().getIdFormulario())
                .nombre(obj.getNombre())
                .metadata(obj.getMetadata())
                .enEdicion(obj.getEnEdicion())
                .build();
    }
     private SeccionResponse toResponseList(Seccion obj) {
        return SeccionResponse.builder()
                .idSeccion(obj.getIdSeccion())
                .codigo(obj.getCodigo())
                .idFormulario(obj.getFormulario().getIdFormulario())
                .nombre(obj.getNombre())
                .build();
    }

    @Override
    @Transactional
    public SeccionResponse create(SeccionCreateRequest request) {
        Formulario formulario = formularioRepository.findById(request.idFormulario())
                .orElseThrow(() -> new NotFoundException("Formulario not found. id=" + request.idFormulario()));

        if (seccionRepository.existsByCodigoAndFormulario_IdFormulario(request.codigo(), request.idFormulario())) {
            throw new ConflictException("Ya existe una sección con código '" + request.codigo() + "' en el formulario id=" + request.idFormulario());
        }

        Seccion entity = Seccion.builder()
                .codigo(request.codigo())
                .formulario(formulario)
                .nombre(request.nombre())
                .metadata(request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode())
                .enEdicion(request.enEdicion() != null ? request.enEdicion() : Boolean.FALSE)
                .build();

        return toResponse(seccionRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public SeccionResponse getById(Integer id) {
        Seccion entity = seccionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seccion not found. id=" + id));

        // Tomar el metadata original del formulario padre y reemplazar
        // el array "secciones" con únicamente la metadata de esta sección.
        Formulario formulario = entity.getFormulario();
        JsonNode formularioMetadata = formulario.getMetadata();

        ObjectNode mergedMetadata;
        if (formularioMetadata != null && formularioMetadata.isObject()) {
            mergedMetadata = formularioMetadata.deepCopy();
        } else {
            mergedMetadata = JsonNodeFactory.instance.objectNode();
        }

        ArrayNode seccionesArray = JsonNodeFactory.instance.arrayNode();
        seccionesArray.add(entity.getMetadata());
        mergedMetadata.set("secciones", seccionesArray);

        return SeccionResponse.builder()
                .idSeccion(entity.getIdSeccion())
                .codigo(entity.getCodigo())
                .idFormulario(formulario.getIdFormulario())
                .nombre(entity.getNombre())
                .metadata(mergedMetadata)
                .enEdicion(entity.getEnEdicion())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponse> list() {
        return seccionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeccionResponse> listByFormulario(Integer idFormulario) {
        if (!formularioRepository.existsById(idFormulario)) {
            throw new NotFoundException("Formulario not found. id=" + idFormulario);
        }
        return seccionRepository.findByFormulario_IdFormulario(idFormulario).stream().map(this::toResponseList).toList();
    }

    @Override
    @Transactional
    public SeccionResponse update(Integer id, SeccionUpdateRequest request) {
        Seccion entity = seccionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seccion not found. id=" + id));

        if (seccionRepository.existsByCodigoAndFormulario_IdFormularioAndIdSeccionNot(
                request.codigo(), entity.getFormulario().getIdFormulario(), id)) {
            throw new ConflictException("Ya existe otra sección con código '" + request.codigo() + "' en el mismo formulario");
        }

        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        entity.setMetadata(request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode());
        entity.setEnEdicion(request.enEdicion() != null ? request.enEdicion() : Boolean.FALSE);
        return toResponse(seccionRepository.save(entity));
    }

    @Override
    @Transactional
    public JsonNode checkout(Integer idSeccion) {
        Seccion seccion = seccionRepository.findById(idSeccion)
                .orElseThrow(() -> new NotFoundException("Seccion not found. id=" + idSeccion));

        if (Boolean.TRUE.equals(seccion.getEnEdicion())) {
            throw new ConflictException("La sección ya se encuentra en edición. id=" + idSeccion);
        }

        seccion.setEnEdicion(Boolean.TRUE);
        seccionRepository.save(seccion);
        return seccion.getMetadata();
    }

    @Override
    @Transactional
    public SeccionResponse save(Integer idSeccion, SeccionSaveRequest request) {
        Seccion seccion = seccionRepository.findById(idSeccion)
                .orElseThrow(() -> new NotFoundException("Seccion not found. id=" + idSeccion));

        seccion.setMetadata(request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode());
        seccion.setEnEdicion(Boolean.FALSE);
        return toResponse(seccionRepository.save(seccion));
    }
}
