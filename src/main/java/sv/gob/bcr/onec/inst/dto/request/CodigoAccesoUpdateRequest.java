package sv.gob.bcr.onec.inst.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CodigoAccesoUpdateRequest")
public record CodigoAccesoUpdateRequest(
        @Schema(description = "ID de la secci\u00f3n a la que pertenece el c\u00f3digo. Si se omite, no se modifica.", example = "2")
        Integer idSeccion,
        @NotNull
        @Schema(description = "Estado activo del código.", example = "false")
        Boolean activo
) { }
