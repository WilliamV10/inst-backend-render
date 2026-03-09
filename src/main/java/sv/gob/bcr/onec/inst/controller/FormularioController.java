package sv.gob.bcr.onec.inst.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.gob.bcr.onec.inst.controller.api.FormularioApi;
import sv.gob.bcr.onec.inst.dto.request.FormularioCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.FormularioUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.FormularioResponse;
import sv.gob.bcr.onec.inst.service.interfaces.FormularioService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forms")
public class FormularioController implements FormularioApi {

    private final FormularioService service;

    @PostMapping
    public ResponseEntity<FormularioResponse> create(@Valid @RequestBody FormularioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormularioResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<FormularioResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormularioResponse> update(@PathVariable Integer id, @Valid @RequestBody FormularioUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
