package sv.gob.bcr.onec.inst.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoBulkCreateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;
import sv.gob.bcr.onec.inst.entity.CodigoAcceso;
import sv.gob.bcr.onec.inst.repository.CodigoAccesoRepository;
import sv.gob.bcr.onec.inst.service.interfaces.CodigoAccesoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CodigoAccesoServiceImpl implements CodigoAccesoService {

    private final CodigoAccesoRepository repository;
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
                .codigo(obj.getCodigo())
                .activo(obj.getActivo())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validate(String codigo) {
        return repository.findByCodigo(codigo)
                .map(c -> Boolean.TRUE.equals(c.getActivo()))
                .orElse(false);
    }

    @Override
    @Transactional
    public List<CodigoAccesoResponse> createBulk(CodigoAccesoBulkCreateRequest request) {
        List<CodigoAcceso> entities = new java.util.ArrayList<>(request.cantidad());
        for (int i = 0; i < request.cantidad(); i++) {
            entities.add(CodigoAcceso.builder()
                    .codigo(generarCodigoUnico())
                    .activo(Boolean.TRUE)
                    .build());
        }
        return repository.saveAll(entities).stream().map(this::toResponse).toList();
    }

}
