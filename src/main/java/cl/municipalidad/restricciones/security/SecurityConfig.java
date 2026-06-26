package cl.municipalidad.restricciones.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en tus controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF si estás usando tokens JWT (común en microservicios REST)
            .csrf(csrf -> csrf.disable())
            
            // Configurar la gestión de sesiones como Stateless (sin estado) para JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Reglas de autorización de rutas
            .authorizeHttpRequests(auth -> auth
                // 🔓 Rutas públicas indispensables de Swagger UI y OpenAPI Docs locales
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                
                // 🚫 APERTURA EXPLÍCITA PARA EL GATEWAY (Mapeo exacto + sub-rutas con comodín)
                .requestMatchers(
                    "/api/v1/restricciones/v3/api-docs",
                    "/api/v1/restricciones/v3/api-docs/**"
                ).permitAll()
                
                // 🔒 El resto de los endpoints requerirá token válido
                .anyRequest().authenticated()
            );

        // NOTA: Si tienes un filtro personalizado para validar el Token JWT (ej: JwtAuthenticationFilter),
        // deberías añadirlo aquí abajo, por ejemplo:
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}