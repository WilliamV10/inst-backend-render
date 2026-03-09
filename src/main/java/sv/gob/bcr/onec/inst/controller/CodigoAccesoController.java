package sv.gob.bcr.onec.inst.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.gob.bcr.onec.inst.controller.api.CodigoAccesoApi;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoCreateRequest;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoUpdateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;
import sv.gob.bcr.onec.inst.service.interfaces.CodigoAccesoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/access-codes")
public class CodigoAccesoController implements CodigoAccesoApi {

    private final CodigoAccesoService service;

    @PostMapping
    public ResponseEntity<CodigoAccesoResponse> create(@Valid @RequestBody CodigoAccesoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodigoAccesoResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CodigoAccesoResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CodigoAccesoResponse> update(@PathVariable Integer id, @Valid @RequestBody CodigoAccesoUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
