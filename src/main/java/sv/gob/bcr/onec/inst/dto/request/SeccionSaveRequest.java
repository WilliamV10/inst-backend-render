package sv.gob.bcr.onec.inst.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "SeccionSaveRequest")
public record SeccionSaveRequest(

        @NotNull
        @Schema(description = "Contenido actualizado de la sección en formato JSON.")
        JsonNode metadata
) { }
