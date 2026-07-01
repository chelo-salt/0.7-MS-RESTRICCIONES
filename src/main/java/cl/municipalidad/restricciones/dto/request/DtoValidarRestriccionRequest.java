package cl.municipalidad.restricciones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Petición para evaluar las restricciones vigentes de un usuario antes de confirmar una reserva")
public class DtoValidarRestriccionRequest {

    @NotNull(message = "El ID de usuario no puede ser nulo.")
    @Positive(message = "El ID de usuario debe ser un número positivo.")
    @Schema(description = "ID único del vecino/usuario en la base de datos", example = "1024")
    private Long idUsuario;

    @NotNull(message = "La fecha de la reserva es obligatoria.")
    @Schema(description = "Fecha calendarizada en la que se desea ocupar el recinto deportivo", example = "2026-07-15")
    private LocalDate fechaReserva;
}