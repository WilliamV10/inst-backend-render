package sv.gob.bcr.onec.inst.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.gob.bcr.onec.inst.controller.api.CodigoAccesoApi;
import sv.gob.bcr.onec.inst.dto.request.CodigoAccesoBulkCreateRequest;
import sv.gob.bcr.onec.inst.dto.response.CodigoAccesoResponse;
import sv.gob.bcr.onec.inst.service.interfaces.CodigoAccesoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/access-codes")
public class CodigoAccesoController implements CodigoAccesoApi {

    private final CodigoAccesoService service;

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String codigo) {
        return ResponseEntity.ok(service.validate(codigo));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<CodigoAccesoResponse>> createBulk(@Valid @RequestBody CodigoAccesoBulkCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBulk(request));
    }
}
