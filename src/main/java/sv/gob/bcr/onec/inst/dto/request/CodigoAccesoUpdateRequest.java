package sv.gob.bcr.onec.inst.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CodigoAccesoUpdateRequest")
public record CodigoAccesoUpdateRequest(
        @NotNull
        @Schema(description = "Estado activo del código.", example = "false")
        Boolean activo
) { }
