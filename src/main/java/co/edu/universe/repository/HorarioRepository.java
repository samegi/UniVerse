package co.edu.universe.repository;

import co.edu.universe.model.Clase;
import co.edu.universe.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
    List<Horario> findByClases_Id(Long claseId);
}
