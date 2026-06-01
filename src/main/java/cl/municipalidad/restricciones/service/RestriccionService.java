package cl.municipalidad.restricciones.service;

import cl.municipalidad.restricciones.dto.request.DtoValidarRestriccionRequest;
import cl.municipalidad.restricciones.dto.response.DtoValidarRestriccionResponse;
import cl.municipalidad.restricciones.model.RestriccionModel;
import cl.municipalidad.restricciones.repository.RestriccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestriccionService {

    private final RestriccionRepository restriccionRepository;

    public DtoValidarRestriccionResponse evaluarReglas(DtoValidarRestriccionRequest request) {
        

        List<RestriccionModel> bloqueosActivos = restriccionRepository.findByIdUsuarioAndActivaTrue(request.getIdUsuario());
        
        DtoValidarRestriccionResponse response = new DtoValidarRestriccionResponse();

        if (!bloqueosActivos.isEmpty()) {
            RestriccionModel bloqueo = bloqueosActivos.get(0);
            response.setAprobado(false);
            response.setAplicaDescuento(false);
            response.setMensaje("Reserva Rechazada: El usuario cuenta con una restricción de tipo [" 
                    + bloqueo.getTipoRestriccion() + "] debido a: " + bloqueo.getMotivo());
            return response;
        }

       
        String tipoUsuario = "PERSONA_NATURAL";
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof Map<?, ?> details) {
            String tipoToken = (String) details.get("tipoUsuario");
            if (tipoToken != null) {
                tipoUsuario = tipoToken;
            }
        }

        response.setAprobado(true);

    
        if ("PERSONA_NATURAL".equals(tipoUsuario)) {
            response.setAplicaDescuento(true);
            response.setMensaje("Usuario habilitado exitosamente. Tipo: PERSONA_NATURAL. ¡Aplica beneficio del 20% de descuento!");
        } else {
            response.setAplicaDescuento(false);
            response.setMensaje("Usuario habilitado exitosamente. Tipo: " + tipoUsuario + ". Aplica cobro de tarifa institucional normal.");
        }

        return response;
    }
}