package sv.gob.bcr.onec.inst.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;
import sv.gob.bcr.onec.inst.entity.CodigoAcceso;
import sv.gob.bcr.onec.inst.entity.Seccion;
import sv.gob.bcr.onec.inst.exception.ConflictException;
import sv.gob.bcr.onec.inst.exception.NotFoundException;
import sv.gob.bcr.onec.inst.repository.CodigoAccesoRepository;
import sv.gob.bcr.onec.inst.repository.SeccionRepository;
import sv.gob.bcr.onec.inst.service.interfaces.CodigoAccesoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodigoAccesoServiceImpl implements CodigoAccesoService {

    private final CodigoAccesoRepository repository;
    private final SeccionRepository seccionRepository;
    private final SecureRandom random = new SecureRandom();

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = String.format("%06d", random.nextInt(1_000_000));
        } while (repository.existsByCodigo(codigo));
        return codigo;
    }

    private CodigoAccesoResponse toResponse(CodigoAcceso obj) {
        return CodigoAccesoResponse.builder()
                .idCodigoAcceso(obj.getIdCodigoAcceso())
                .idSeccion(obj.getSeccion().getIdSeccion())
                .codigo(obj.getCodigo())
                .activo(obj.getActivo())
                .build();
    }

    @Override
    @Transactional
    public CodigoAccesoResponse create(CodigoAccesoCreateRequest request) {
        String codigo = (request.codigo() != null) ? request.codigo() : generarCodigoUnico();

        if (repository.existsByCodigo(codigo)) {
            throw new ConflictException("Ya existe un código de acceso con el valor: " + codigo);
        }

        Seccion seccion = seccionRepository.findById(request.idSeccion())
                .orElseThrow(() -> new NotFoundException("Sección no encontrada. id=" + request.idSeccion()));

        CodigoAcceso entity = CodigoAcceso.builder()
                .seccion(seccion)
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

        if (request.idSeccion() != null) {
            Seccion seccion = seccionRepository.findById(request.idSeccion())
                    .orElseThrow(() -> new NotFoundException("Sección no encontrada. id=" + request.idSeccion()));
            entity.setSeccion(seccion);
        }

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
