package cl.municipalidad.restricciones.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoValidarRestriccionRequest {

    @NotNull(message = "El ID de usuario no puede ser nulo.")
    @Positive(message = "El ID de usuario debe ser un número positivo.")
    private Long idUsuario;

    @NotNull(message = "La fecha de la reserva es obligatoria.")
    private LocalDate fechaReserva;
}