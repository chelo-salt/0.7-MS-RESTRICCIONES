package cl.municipalidad.restricciones.controller;

import cl.municipalidad.restricciones.dto.request.DtoValidarRestriccionRequest;
import cl.municipalidad.restricciones.dto.response.DtoValidarRestriccionResponse;
import cl.municipalidad.restricciones.service.RestriccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restricciones")
@RequiredArgsConstructor
public class RestriccionController {

    private final RestriccionService restriccionService;

    @PostMapping("/validar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DtoValidarRestriccionResponse> validarReglasUsuario(
            @Valid @RequestBody DtoValidarRestriccionRequest request) {
        
        DtoValidarRestriccionResponse response = restriccionService.evaluarReglas(request);
        return ResponseEntity.ok(response);
    }
}