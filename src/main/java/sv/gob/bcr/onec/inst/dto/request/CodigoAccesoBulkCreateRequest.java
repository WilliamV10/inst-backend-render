package sv.gob.bcr.onec.inst.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CodigoAccesoBulkCreateRequest")
public record CodigoAccesoBulkCreateRequest(

        @NotNull(message = "La cantidad es requerida")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @Schema(description = "Número de códigos de acceso a generar", example = "10")
        Integer cantidad
) { }
