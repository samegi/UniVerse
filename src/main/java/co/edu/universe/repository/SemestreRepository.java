package co.edu.universe.repository;
import java.time.Instant;
import java.util.Optional;

import co.edu.universe.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {

    /**
     * Busca el semestre cuya fecha de inicio sea posterior a la actual,
     * ordenando de menor a mayor, y devuelve el más próximo.
     *
     * Ejemplo de uso: obtener el próximo semestre futuro.
     */
    Optional<Semestre> findTopByFechaInicioAfterOrderByFechaInicioAsc(Instant fecha);
}

