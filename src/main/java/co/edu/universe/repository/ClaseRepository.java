package co.edu.universe.repository;

import co.edu.universe.model.Asignatura;
import co.edu.universe.model.Clase;
import co.edu.universe.model.DiaSemana;
import co.edu.universe.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClaseRepository extends JpaRepository<Clase, Long> {

    // Buscar clase por ID con todas sus relaciones cargadas
    @Query("SELECT c FROM Clase c " +
           "LEFT JOIN FETCH c.semestre " +
           "LEFT JOIN FETCH c.salon " +
           "LEFT JOIN FETCH c.asignatura " +
           "WHERE c.id = :id")
    Optional<Clase> findByIdWithRelations(Long id);

    // Buscar todas las clases con todas sus relaciones cargadas
    @Query("SELECT DISTINCT c FROM Clase c " +
           "LEFT JOIN FETCH c.semestre " +
           "LEFT JOIN FETCH c.salon " +
           "LEFT JOIN FETCH c.asignatura " +
           "LEFT JOIN FETCH c.asignaciones a " +
           "LEFT JOIN FETCH a.profesor")
    List<Clase> findAllWithRelations();

    // Buscar todas las clases de un semestre específico
    List<Clase> findBySemestre(Semestre semestre);

    // Buscar todas las clases de una asignatura específica
    List<Clase> findByAsignatura(Asignatura asignatura);
    // Buscar todas las clases de una asignatura por AsignaturaId
    List<Clase> findByAsignaturaId(Long asignaturaId);
    @Query("""
    SELECT COUNT(c) > 0
    FROM Clase c
    WHERE c.salon.id = :salonId
      AND c.dia = :dia
      AND (
            (c.horaInicio < :horaFin AND c.horaFin > :horaInicio)
      )
      AND c.id <> :idClase
""")
    boolean existeChoqueDeHorario(
            Long salonId,
            DiaSemana dia,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long idClase
    );

}
