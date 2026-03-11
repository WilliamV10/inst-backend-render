package sv.gob.bcr.onec.inst.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String descripcion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private JsonNode metadata;
}
