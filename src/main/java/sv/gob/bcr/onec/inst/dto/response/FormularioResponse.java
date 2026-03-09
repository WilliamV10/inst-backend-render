package sv.gob.bcr.onec.inst.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(name = "FormularioResponse")
public class FormularioResponse {
    private Integer idFormulario;
    private String codigo;
    private String nombre;
    private JsonNode metadata;
}
