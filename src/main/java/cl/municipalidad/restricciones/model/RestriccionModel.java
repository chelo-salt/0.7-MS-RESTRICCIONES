package cl.municipalidad.restricciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "restricciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestriccionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idUsuario; // ID del vecino al que se le aplica la regla

    @Column(nullable = false)
    private String tipoRestriccion; // Ej: "BLOQUEO_RESERVAS", "MAX_RESERVAS_SEMANALES"

    @Column(nullable = false)
    private String motivo; // Ej: "Inasistencia reiterada a partidos"

    @Column(nullable = false)
    private boolean activa;

    private LocalDateTime fechaExpiracion; // Cuándo se levanta el castigo (puede ser null)
}