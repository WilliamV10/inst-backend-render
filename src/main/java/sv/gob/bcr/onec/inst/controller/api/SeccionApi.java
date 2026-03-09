package sv.gob.bcr.onec.inst.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import sv.gob.bcr.onec.inst.dto.request.SeccionCheckoutRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionSaveRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.SeccionResponse;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@Tag(name = "Sections")
public interface SeccionApi {

    @Operation(summary = "Create section")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "404", description = "Formulario not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict - section code already exists in form", content = @Content)
    ResponseEntity<SeccionResponse> create(@Valid @RequestBody SeccionCreateRequest request);

    @Operation(summary = "Get section by id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<SeccionResponse> getById(@PathVariable Integer id);

    @Operation(summary = "List sections. Filter by formulario with ?idFormulario=")
    ResponseEntity<List<SeccionResponse>> list(@RequestParam(required = false) Integer idFormulario);

    @Operation(summary = "Update section")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflict - section code already exists in form", content = @Content)
    ResponseEntity<SeccionResponse> update(@PathVariable Integer id, @Valid @RequestBody SeccionUpdateRequest request);

    @Operation(summary = "Delete section")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ResponseEntity<Void> delete(@PathVariable Integer id);

    @Operation(summary = "Checkout section for editing",
            description = "Verifica el código de acceso y que la sección no esté en edición. " +
                          "Si todo es correcto, bloquea la sección (en_edicion=true) y devuelve su metadata.")
    @ApiResponse(responseCode = "200", description = "OK - metadata de la sección")
    @ApiResponse(responseCode = "404", description = "Sección o código de acceso no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Código inactivo o sección ya en edición", content = @Content)
    ResponseEntity<JsonNode> checkout(@PathVariable Integer id, @Valid @RequestBody SeccionCheckoutRequest request);

    @Operation(summary = "Save section changes",
            description = "Actualiza la metadata de la sección y la libera (en_edicion=false).")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Sección no encontrada", content = @Content)
    ResponseEntity<SeccionResponse> save(@PathVariable Integer id, @Valid @RequestBody SeccionSaveRequest request);
}
