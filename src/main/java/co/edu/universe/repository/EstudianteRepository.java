package co.edu.universe.repository;
import co.edu.universe.model.Estudiante;
import co.edu.universe.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    List<Estudiante> usuario(Usuario usuario);
    @Query("""
    SELECT c.id, COUNT(e)
    FROM Estudiante e
    JOIN e.horario h
    JOIN h.clases c
    GROUP BY c.id
""")
    List<Object[]> countEstudiantesPorClase();
    @Query("""
    SELECT DISTINCT e FROM Estudiante e
    JOIN FETCH e.horario h
    LEFT JOIN FETCH h.clases c
    LEFT JOIN FETCH c.asignatura a
    LEFT JOIN FETCH c.salon s
    WHERE e.id = :id
""")
    Optional<Estudiante> obtenerEstudianteCompleto(Long id);

}

