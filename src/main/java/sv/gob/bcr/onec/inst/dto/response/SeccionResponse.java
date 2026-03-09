package sv.gob.bcr.onec.inst.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(name = "SeccionResponse")
public class SeccionResponse {
    private Integer idSeccion;
    private String codigo;
    private Integer idFormulario;
    private String nombre;
    private JsonNode metadata;
    private Boolean enEdicion;
}
