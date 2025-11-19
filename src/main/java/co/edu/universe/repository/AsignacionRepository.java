package co.edu.universe.repository;

import co.edu.universe.model.Asignacion;
import co.edu.universe.model.Clase;
import co.edu.universe.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    List<Asignacion> findByClase(Clase clase);
    List<Asignacion> findByProfesor(Profesor profesor);
    Optional<Asignacion> findByProfesorAndClase(Profesor profesor, Clase clase);
}
