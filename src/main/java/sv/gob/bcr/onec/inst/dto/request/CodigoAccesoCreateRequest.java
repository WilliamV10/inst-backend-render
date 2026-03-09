package sv.gob.bcr.onec.inst.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(name = "CodigoAccesoCreateRequest")
public record CodigoAccesoCreateRequest(

        @Pattern(regexp = "^[0-9]{6}$", message = "El código debe ser exactamente 6 dígitos numéricos")
        @Schema(description = "Código de 6 dígitos. Si se omite, se genera automáticamente.", example = "482719")
        String codigo,

        @Schema(description = "Estado activo del código. Por defecto true.", example = "true")
        Boolean activo
) { }
