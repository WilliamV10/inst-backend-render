package sv.gob.bcr.onec.inst.service.interfaces;

import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoBulkCreateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;

import java.util.List;

public interface CodigoAccesoService {
    boolean validate(String codigo);
    List<CodigoAccesoResponse> createBulk(CodigoAccesoBulkCreateRequest request);
}
