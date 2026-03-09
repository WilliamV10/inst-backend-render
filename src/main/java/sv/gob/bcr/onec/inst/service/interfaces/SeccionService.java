package sv.gob.bcr.onec.inst.service.interfaces;

import sv.gob.bcr.onec.inst.dto.request.SeccionCheckoutRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionSaveRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.SeccionResponse;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public interface SeccionService {
    SeccionResponse create(SeccionCreateRequest request);
    SeccionResponse getById(Integer id);
    List<SeccionResponse> list();
    List<SeccionResponse> listByFormulario(Integer idFormulario);
    SeccionResponse update(Integer id, SeccionUpdateRequest request);
    void delete(Integer id);

    /** Verifica el código de acceso, bloquea la sección (en_edicion=true) y devuelve su metadata. */
    JsonNode checkout(Integer idSeccion, SeccionCheckoutRequest request);

    /** Guarda el metadata actualizado y libera la sección (en_edicion=false). */
    SeccionResponse save(Integer idSeccion, SeccionSaveRequest request);
}
