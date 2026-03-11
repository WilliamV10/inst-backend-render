package sv.gob.bcr.onec.inst.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "FormularioCreateRequest")
public record FormularioCreateRequest(

        @NotBlank
        @Size(max = 50)
        @Schema(description = "Código único del formulario.", example = "FORM-001")
        String codigo,

        @NotBlank
        @Size(max = 255)
        @Schema(description = "Nombre descriptivo del formulario.", example = "Formulario de Registro")
        String nombre,

        @Schema(description = "Descripción del formulario.")
        String descripcion,

        @Schema(description = "Metadata adicional en formato JSON.")
        JsonNode metadata
) { }
