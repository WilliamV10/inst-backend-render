package sv.gob.bcr.onec.inst.service.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.bcr.onec.inst.dto.request.FormularioCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.FormularioUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.FormularioResponse;
import sv.gob.bcr.onec.inst.entity.Formulario;
import sv.gob.bcr.onec.inst.exception.ConflictException;
import sv.gob.bcr.onec.inst.exception.NotFoundException;
import sv.gob.bcr.onec.inst.repository.FormularioRepository;
import sv.gob.bcr.onec.inst.service.interfaces.FormularioService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormularioServiceImpl implements FormularioService {

    private final FormularioRepository repository;

    private FormularioResponse toResponse(Formulario obj) {
        return FormularioResponse.builder()
                .idFormulario(obj.getIdFormulario())
                .codigo(obj.getCodigo())
                .nombre(obj.getNombre())
                .metadata(obj.getMetadata())
                .build();
    }

    @Override
    @Transactional
    public FormularioResponse create(FormularioCreateRequest request) {
        if (repository.existsByCodigo(request.codigo())) {
            throw new ConflictException("Ya existe un formulario con el código: " + request.codigo());
        }

        Formulario entity = Formulario.builder()
                .codigo(request.codigo())
                .nombre(request.nombre())
                .metadata(request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode())
                .build();

        return toResponse(repository.save(entity));
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
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public FormularioResponse update(Integer id, FormularioUpdateRequest request) {
        Formulario entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Formulario not found. id=" + id));

        if (repository.existsByCodigoAndIdFormularioNot(request.codigo(), id)) {
            throw new ConflictException("Ya existe otro formulario con el código: " + request.codigo());
        }

        entity.setCodigo(request.codigo());
        entity.setNombre(request.nombre());
        entity.setMetadata(request.metadata() != null ? request.metadata() : JsonNodeFactory.instance.objectNode());
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Formulario not found. id=" + id);
        }
        repository.deleteById(id);
    }
}
