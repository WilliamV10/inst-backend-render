package sv.gob.bcr.onec.inst.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(name = "CodigoAccesoResponse")
public class CodigoAccesoResponse {
    private Integer idCodigoAcceso;
    private Integer idSeccion;
    private String codigo;
    private Boolean activo;
}
