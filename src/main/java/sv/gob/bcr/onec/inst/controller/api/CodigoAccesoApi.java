package sv.gob.bcr.onec.inst.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoBulkCreateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;

import java.util.List;

@Tag(name = "Access Codes")
public interface CodigoAccesoApi {

    @Operation(summary = "Validate if an access code exists and is active")
    @ApiResponse(responseCode = "200", description = "true if exists and active, false otherwise")
    ResponseEntity<Boolean> validate(@RequestParam String codigo);

    @Operation(summary = "Bulk create N access codes (auto-generated 6-digit codes)")
    @ApiResponse(responseCode = "201", description = "Created")
    ResponseEntity<List<CodigoAccesoResponse>> createBulk(@Valid @RequestBody CodigoAccesoBulkCreateRequest request);
}
