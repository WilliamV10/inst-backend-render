package sv.gob.bcr.onec.inst.service.interfaces;

import sv.gob.bcr.onec.inst.dto.request.FormularioCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.FormularioUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.FormularioResponse;

import java.util.List;

public interface FormularioService {
    FormularioResponse create(FormularioCreateRequest request);
    FormularioResponse getById(Integer id);
    List<FormularioResponse> list();
    FormularioResponse update(Integer id, FormularioUpdateRequest request);
    void delete(Integer id);
}
