package cl.municipalidad.restricciones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClientAuth() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080") // 🔒 Apunta directo al puerto de ms-auth
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}