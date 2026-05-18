package cl.municipalidad.restricciones.service;

import cl.municipalidad.restricciones.client.AuthClient;
import cl.municipalidad.restricciones.dto.request.DtoValidarRestriccionRequest;
import cl.municipalidad.restricciones.dto.response.DtoValidarRestriccionResponse;
import cl.municipalidad.restricciones.model.RestriccionModel;
import cl.municipalidad.restricciones.repository.RestriccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // 🚀 Genera el constructor para inyectar el repositorio y el cliente HTTP
public class RestriccionService {

    private final RestriccionRepository restriccionRepository;
    private final AuthClient authClient;

    public DtoValidarRestriccionResponse evaluarReglas(DtoValidarRestriccionRequest request) {
        
        // 1. 🗄️ Consultamos nuestra BD local buscando si el usuario tiene bloqueos activos
        List<RestriccionModel> bloqueosActivos = restriccionRepository.findByIdUsuarioAndActivaTrue(request.getIdUsuario());
        
        DtoValidarRestriccionResponse response = new DtoValidarRestriccionResponse();

        // Si la lista no está vacía, significa que el vecino está sancionado/bloqueado
        if (!bloqueosActivos.isEmpty()) {
            RestriccionModel bloqueo = bloqueosActivos.get(0); // Tomamos el primer motivo de ejemplo
            response.setAprobado(false);
            response.setAplicaDescuento(false);
            response.setMensaje("Reserva Rechazada: El usuario cuenta con una restricción de tipo [" 
                    + bloqueo.getTipoRestriccion() + "] debido a: " + bloqueo.getMotivo());
            return response;
        }

        // 2. 📡 Si pasó el filtro de bloqueos, llamamos a ms-auth por red usando WebClient para validar residencia
        // Usamos .block() para convertir la llamada reactiva en síncrona, tal como hace el profesor en ms-pagos
        Boolean esResidente = authClient.verificarResidencia(request.getIdUsuario()).block();

        response.setAprobado(true);
        if (Boolean.TRUE.equals(esResidente)) {
            response.setAplicaDescuento(true);
            response.setMensaje("Usuario habilitado de forma exitosa. ¡Aplica descuento del 50% por residencia!");
        } else {
            response.setAplicaDescuento(false);
            response.setMensaje("Usuario habilitado de forma exitosa. Tarifa normal (No es residente verificado).");
        }

        return response;
    }
}