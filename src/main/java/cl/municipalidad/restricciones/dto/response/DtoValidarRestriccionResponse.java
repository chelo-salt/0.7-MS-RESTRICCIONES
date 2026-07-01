package cl.municipalidad.restricciones.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta del motor de reglas del municipio que define penalizaciones o beneficios aplicables")
public class DtoValidarRestriccionResponse {
    
    @Schema(description = "Indica si el usuario cumple las condiciones para arrendar (true si está habilitado, false si está bloqueado)", example = "true")
    private boolean aprobado;

    @Schema(description = "Determina si el usuario califica como residente certificado para obtener una rebaja en la tarifa", example = "true")
    private boolean aplicaDescuento;

    @Schema(description = "Detalle del resultado de la evaluación de restricciones", example = "Usuario habilitado exitosamente. Tipo: PERSONA_NATURAL. ¡Aplica beneficio del 20% de descuento!")
    private String mensaje;
}