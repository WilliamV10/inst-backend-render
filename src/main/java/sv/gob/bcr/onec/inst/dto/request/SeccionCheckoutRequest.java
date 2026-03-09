package sv.gob.bcr.onec.inst.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(name = "SeccionCheckoutRequest")
public record SeccionCheckoutRequest(

        @NotBlank
        @Size(min = 6, max = 6)
        @Pattern(regexp = "^[0-9]{6}$", message = "El código de acceso debe ser exactamente 6 dígitos numéricos.")
        @Schema(description = "Código de acceso de 6 dígitos para desbloquear la edición de la sección.", example = "123456")
        String codigoAcceso
) { }
