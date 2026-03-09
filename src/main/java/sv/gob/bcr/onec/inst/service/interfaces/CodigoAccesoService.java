package sv.gob.bcr.onec.inst.service.interfaces;

import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;

import java.util.List;

public interface CodigoAccesoService {
    CodigoAccesoResponse create(CodigoAccesoCreateRequest request);
    CodigoAccesoResponse getById(Integer id);
    List<CodigoAccesoResponse> list();
    CodigoAccesoResponse update(Integer id, CodigoAccesoUpdateRequest request);
    void delete(Integer id);
}
