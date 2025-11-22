package co.edu.universe.repository;

import co.edu.universe.model.Asignatura;
import co.edu.universe.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Long> {
}
