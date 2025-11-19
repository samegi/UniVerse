package co.edu.universe.repository;

import co.edu.universe.model.Asignatura;
import co.edu.universe.model.Clase;
import co.edu.universe.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    // Buscar todas las clases de un semestre específico
    List<Clase> findBySemestre(Semestre semestre);

    // Buscar todas las clases de una asignatura específica
    List<Clase> findByAsignatura(Asignatura asignatura);
    // Buscar todas las clases de una asignatura por AsignaturaId
    List<Clase> findByAsignaturaId(Long asignaturaId);

    @Query("""
    SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
    FROM Clase c
    WHERE c.salon.id = :salonId
      AND c.dia = :dia
      AND c.id <> :claseId
      AND (
            (c.horaInicio < :horaFin AND c.horaFin > :horaInicio)
          )
""")
    boolean existeChoqueDeHorario(Long salonId,
                                  String dia,
                                  LocalTime horaInicio,
                                  LocalTime horaFin,
                                  Long claseId);

}
