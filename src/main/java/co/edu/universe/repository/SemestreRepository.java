package co.edu.universe.repository;
import java.time.Instant;
import java.util.Optional;

import co.edu.universe.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {
}

