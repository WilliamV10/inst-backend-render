package sv.gob.bcr.onec.inst.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;
import sv.gob.bcr.onec.inst.entity.CodigoAcceso;
import sv.gob.bcr.onec.inst.exception.ConflictException;
import sv.gob.bcr.onec.inst.exception.NotFoundException;
import sv.gob.bcr.onec.inst.repository.CodigoAccesoRepository;
import sv.gob.bcr.onec.inst.service.interfaces.CodigoAccesoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodigoAccesoServiceImpl implements CodigoAccesoService {

    private final CodigoAccesoRepository repository;

    private CodigoAccesoResponse toResponse(CodigoAcceso obj) {
        return CodigoAccesoResponse.builder()
                .idCodigoAcceso(obj.getIdCodigoAcceso())
                .codigo(obj.getCodigo())
                .activo(obj.getActivo())
                .build();
    }

    @Override
    @Transactional
    public CodigoAccesoResponse create(CodigoAccesoCreateRequest request) {
        String codigo = (request.codigo() != null) ? request.codigo() : repository.generarCodigo();

        if (repository.existsByCodigo(codigo)) {
            throw new ConflictException("Ya existe un código de acceso con el valor: " + codigo);
        }

        CodigoAcceso entity = CodigoAcceso.builder()
                .codigo(codigo)
                .activo(request.activo() != null ? request.activo() : Boolean.TRUE)
                .build();

        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CodigoAccesoResponse getById(Integer id) {
        CodigoAcceso entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("CodigoAcceso not found. id=" + id));
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CodigoAccesoResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public CodigoAccesoResponse update(Integer id, CodigoAccesoUpdateRequest request) {
        CodigoAcceso entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("CodigoAcceso not found. id=" + id));
        entity.setActivo(request.activo());
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("CodigoAcceso not found. id=" + id);
        }
        repository.deleteById(id);
    }
}
