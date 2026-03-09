package sv.gob.bcr.onec.inst.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import sv.gob.bcr.onec.inst.dto.request.FormularioCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.FormularioUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.FormularioResponse;

import java.util.List;

@Tag(name = "Forms")
public interface FormularioApi {

    @Operation(summary = "Create form")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "409", description = "Conflict - code already exists", content = @Content)
    ResponseEntity<FormularioResponse> create(@Valid @RequestBody FormularioCreateRequest request);

    @Operation(summary = "Get form by id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<FormularioResponse> getById(@PathVariable Integer id);

    @Operation(summary = "List all forms")
    ResponseEntity<List<FormularioResponse>> list();

    @Operation(summary = "Update form")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict - code already exists", content = @Content)
    ResponseEntity<FormularioResponse> update(@PathVariable Integer id, @Valid @RequestBody FormularioUpdateRequest request);

    @Operation(summary = "Delete form")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<Void> delete(@PathVariable Integer id);
}
