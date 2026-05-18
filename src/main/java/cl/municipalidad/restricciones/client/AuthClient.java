package cl.municipalidad.restricciones.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {

    private final WebClient webClientAuth;

    // Spring inyecta automáticamente el Bean que configuramos en WebClientConfig
    public AuthClient(WebClient webClientAuth) {
        this.webClientAuth = webClientAuth;
    }

    /**
     * Consulta a ms-auth si el usuario es un residente verificado de la comuna.
     * Retorna un Mono<Boolean> de manera reactiva.
     */
    public Mono<Boolean> verificarResidencia(Long idUsuario) {
        return webClientAuth.get()
                .uri("/api/v1/auth/usuarios/{id}/residencia", idUsuario)
                .retrieve()
                .bodyToMono(Boolean.class)
                // Si ms-auth llega a caerse o no encuentra al usuario, respondemos "false" por seguridad
                .onErrorReturn(false); 
    }
}