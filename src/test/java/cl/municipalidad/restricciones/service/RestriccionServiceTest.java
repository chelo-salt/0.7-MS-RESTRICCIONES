package cl.municipalidad.restricciones.service;

import cl.municipalidad.restricciones.dto.request.DtoValidarRestriccionRequest;
import cl.municipalidad.restricciones.dto.response.DtoValidarRestriccionResponse;
import cl.municipalidad.restricciones.model.RestriccionModel;
import cl.municipalidad.restricciones.repository.RestriccionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestriccionServiceTest {

    @Mock
    private RestriccionRepository restriccionRepository;

    @InjectMocks
    private RestriccionService restriccionService;

    private DtoValidarRestriccionRequest requestBase;

    @BeforeEach
    void setUp() {
        requestBase = new DtoValidarRestriccionRequest();
        requestBase.setIdUsuario(1024L);
        requestBase.setFechaReserva(LocalDate.now().plusDays(2));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // 1. Usuario bloqueado por deudas o suspensiones activas
    @Test
    void evaluarReglas_CuandoUsuarioTieneBloqueos_DebeRechazarReserva() {
        RestriccionModel bloqueo = new RestriccionModel();
        bloqueo.setIdUsuario(1024L);
        bloqueo.setActiva(true);
        bloqueo.setTipoRestriccion("DEUDA_MOROSA");
        bloqueo.setMotivo("No pago de arriendo de cancha de Tenis mes anterior");

        when(restriccionRepository.findByIdUsuarioAndActivaTrue(1024L))
                .thenReturn(List.of(bloqueo));

        DtoValidarRestriccionResponse response = restriccionService.evaluarReglas(requestBase);

        assertFalse(response.isAprobado());
        assertFalse(response.isAplicaDescuento());
        assertTrue(response.getMensaje().contains("Reserva Rechazada"));
        assertTrue(response.getMensaje().contains("DEUDA_MOROSA"));
        verify(restriccionRepository, times(1)).findByIdUsuarioAndActivaTrue(1024L);
    }

    // 2. Vecino residente (PERSONA_NATURAL) califica para el 20% de descuento
    @Test
    void evaluarReglas_CuandoVecinoEsPersonaNatural_DebeAprobarYAplicarDescuento() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("vecino123", null, Collections.emptyList());
        auth.setDetails(Map.of("tipoUsuario", "PERSONA_NATURAL"));
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(restriccionRepository.findByIdUsuarioAndActivaTrue(1024L))
                .thenReturn(Collections.emptyList()); // Sin bloqueos

        DtoValidarRestriccionResponse response = restriccionService.evaluarReglas(requestBase);

        assertTrue(response.isAprobado());
        assertTrue(response.isAplicaDescuento());
        assertTrue(response.getMensaje().contains("¡Aplica beneficio del 20% de descuento!"));
    }

    // 3. Empresa/Institución (PERSONA_JURIDICA) aprueba pero sin descuento comercial
    @Test
    void evaluarReglas_CuandoUsuarioEsPersonaJuridica_DebeAprobarSinDescuento() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("empresaCorp", null, Collections.emptyList());
        auth.setDetails(Map.of("tipoUsuario", "PERSONA_JURIDICA"));
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(restriccionRepository.findByIdUsuarioAndActivaTrue(1024L))
                .thenReturn(Collections.emptyList());

        DtoValidarRestriccionResponse response = restriccionService.evaluarReglas(requestBase);

        assertTrue(response.isAprobado());
        assertFalse(response.isAplicaDescuento());
        assertTrue(response.getMensaje().contains("Tipo: PERSONA_JURIDICA"));
    }

    // 4. Flujo alternativo si los detalles del token están vacíos
    @Test
    void evaluarReglas_CuandoTokenNoTieneDetalles_DebeAsumirPersonaNaturalPorDefecto() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("userDefault", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(restriccionRepository.findByIdUsuarioAndActivaTrue(1024L))
                .thenReturn(Collections.emptyList());

        DtoValidarRestriccionResponse response = restriccionService.evaluarReglas(requestBase);

        assertTrue(response.isAprobado());
        assertTrue(response.isAplicaDescuento()); // Asume PERSONA_NATURAL por defecto en el código
        assertTrue(response.getMensaje().contains("PERSONA_NATURAL"));
    }

    // 5. Verificar el correcto mapeo y aislamiento de llamadas de datos
    @Test
    void evaluarReglas_DebeLlamarCorrectamenteAlMetodoFiltroDelRepository() {
        when(restriccionRepository.findByIdUsuarioAndActivaTrue(1024L))
                .thenReturn(Collections.emptyList());

        restriccionService.evaluarReglas(requestBase);

        verify(restriccionRepository, times(1)).findByIdUsuarioAndActivaTrue(1024L);
        verifyNoMoreInteractions(restriccionRepository);
    }
}