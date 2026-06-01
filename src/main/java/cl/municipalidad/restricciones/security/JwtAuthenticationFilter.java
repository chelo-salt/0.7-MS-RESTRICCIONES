package cl.municipalidad.restricciones.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret:ClaveUltraSecretaEInviolableParaLaMunicipalidad2026}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.replace("Bearer ", "");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();

            Object rolesObject = claims.get("rol");
            if (rolesObject == null) rolesObject = claims.get("rolUsuario");
            if (rolesObject == null) rolesObject = claims.get("roles");

            if (rolesObject != null) {
                if (rolesObject instanceof java.util.Collection<?>) {
                    authorities = ((java.util.Collection<?>) rolesObject).stream()
                            .map(Object::toString)
                            .map(r -> r.toUpperCase().trim())
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                            .map(SimpleGrantedAuthority::new)
                            .toList();
                } else {
                    String rolString = rolesObject.toString().toUpperCase().trim();
                    if (!rolString.startsWith("ROLE_")) {
                        rolString = "ROLE_" + rolString;
                    }
                    authorities = List.of(new SimpleGrantedAuthority(rolString));
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );
                
               
                String tipoUsuario = claims.get("tipoUsuario", String.class);
                if (tipoUsuario == null) {
                    tipoUsuario = claims.get("tipo", String.class);
                }
                if (tipoUsuario != null) {
                    auth.setDetails(Map.of("tipoUsuario", tipoUsuario.toUpperCase().trim()));
                }

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            System.out.println("Error en JwtAuthenticationFilter (Restricciones): " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}