package sv.gob.bcr.onec.inst.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Schema(name = "ApiError")
public class ApiError {
    private String message;
    private String path;
    private int status;
    private OffsetDateTime timestamp;
}
