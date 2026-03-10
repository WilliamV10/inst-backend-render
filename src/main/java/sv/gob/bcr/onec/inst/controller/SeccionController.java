package sv.gob.bcr.onec.inst.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.gob.bcr.onec.inst.controller.api.SeccionApi;
import sv.gob.bcr.onec.inst.dto.request.SeccionCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionSaveRequest;
import sv.gob.bcr.onec.inst.dto.request.SeccionUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.SeccionResponse;
import sv.gob.bcr.onec.inst.service.interfaces.SeccionService;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sections")
public class SeccionController implements SeccionApi {

    private final SeccionService service;

    @PostMapping
    public ResponseEntity<SeccionResponse> create(@Valid @RequestBody SeccionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeccionResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SeccionResponse>> list(@RequestParam(required = false) Integer idFormulario) {
        if (idFormulario != null) {
            return ResponseEntity.ok(service.listByFormulario(idFormulario));
        }
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeccionResponse> update(@PathVariable Integer id, @Valid @RequestBody SeccionUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<JsonNode> checkout(@PathVariable Integer id) {
        return ResponseEntity.ok(service.checkout(id));
    }

    @PutMapping("/{id}/save")
    public ResponseEntity<SeccionResponse> save(@PathVariable Integer id, @Valid @RequestBody SeccionSaveRequest request) {
        return ResponseEntity.ok(service.save(id, request));
    }
}
