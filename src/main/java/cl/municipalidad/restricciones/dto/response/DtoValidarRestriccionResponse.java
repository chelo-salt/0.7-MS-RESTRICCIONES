package cl.municipalidad.restricciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoValidarRestriccionResponse {
    private boolean aprobado;         // true si puede arrendar, false si está bloqueado
    private boolean aplicaDescuento;  // true si ms-auth confirmó que es residente de la comuna
    private String mensaje;           // Mensaje aclaratorio (Ej: "Usuario habilitado" o "Bloqueado por deudas")
}