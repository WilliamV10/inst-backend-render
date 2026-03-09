package sv.gob.bcr.onec.inst.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "SeccionCreateRequest")
public record SeccionCreateRequest(

        @NotBlank
        @Size(max = 50)
        @Schema(description = "Código único de la sección dentro del formulario.", example = "SEC-01")
        String codigo,

        @NotNull
        @Schema(description = "ID del formulario al que pertenece la sección.", example = "1")
        Integer idFormulario,

        @NotBlank
        @Size(max = 255)
        @Schema(description = "Nombre de la sección.", example = "Datos Personales")
        String nombre,

        @Schema(description = "Metadata adicional en formato JSON.")
        JsonNode metadata,

        @NotNull
        @Schema(description = "Indica si la sección está en edición.", example = "false")
        Boolean enEdicion
) { }
