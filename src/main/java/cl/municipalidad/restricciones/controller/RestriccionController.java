package cl.municipalidad.restricciones.controller;

import cl.municipalidad.restricciones.dto.request.DtoValidarRestriccionRequest;
import cl.municipalidad.restricciones.dto.response.DtoValidarRestriccionResponse;
import cl.municipalidad.restricciones.service.RestriccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restricciones")
@RequiredArgsConstructor
@Tag(name = "Motor de Restricciones", description = "Endpoints de control urbano para evaluar deudas, suspensiones y beneficios fiscales de los usuarios")
public class RestriccionController {

    private final RestriccionService restriccionService;

    @Operation(summary = "Validar reglas y restricciones del usuario", description = "Evalúa de manera transaccional si el vecino posee bloqueos vigentes o si califica para descuentos especiales de residente.")
    @ApiResponse(responseCode = "200", description = "Evaluación del motor de reglas procesada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o faltantes")
    @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT ausente o inválido")
    @PostMapping("/validar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DtoValidarRestriccionResponse> validarReglasUsuario(
            @Valid @RequestBody DtoValidarRestriccionRequest request) {
        
        DtoValidarRestriccionResponse response = restriccionService.evaluarReglas(request);
        return ResponseEntity.ok(response);
    }
}