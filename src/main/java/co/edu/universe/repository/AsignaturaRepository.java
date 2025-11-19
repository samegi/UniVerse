package co.edu.universe.repository;

import co.edu.universe.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    boolean existsByNombre(String nombre);
}

