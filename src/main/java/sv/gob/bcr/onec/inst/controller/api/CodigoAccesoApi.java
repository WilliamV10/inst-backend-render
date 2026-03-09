package sv.gob.bcr.onec.inst.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;

import java.util.List;

@Tag(name = "Access Codes")
public interface CodigoAccesoApi {

    @Operation(summary = "Create access code (auto-generates code if not provided)")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "409", description = "Conflict - code already exists", content = @Content)
    ResponseEntity<CodigoAccesoResponse> create(@Valid @RequestBody CodigoAccesoCreateRequest request);

    @Operation(summary = "Get access code by id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<CodigoAccesoResponse> getById(@PathVariable Integer id);

    @Operation(summary = "List all access codes")
    ResponseEntity<List<CodigoAccesoResponse>> list();

    @Operation(summary = "Update access code (toggle activo)")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<CodigoAccesoResponse> update(@PathVariable Integer id, @Valid @RequestBody CodigoAccesoUpdateRequest request);

    @Operation(summary = "Delete access code")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<Void> delete(@PathVariable Integer id);
}
