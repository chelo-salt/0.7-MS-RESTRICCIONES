package cl.municipalidad.restricciones.repository;

import cl.municipalidad.restricciones.model.RestriccionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestriccionRepository extends JpaRepository<RestriccionModel, Long> {
    // Busca todas las restricciones activas de un vecino en particular
    List<RestriccionModel> findByIdUsuarioAndActivaTrue(Long idUsuario);
}