package co.edu.universe.repository;
import co.edu.universe.model.Estudiante;
import co.edu.universe.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    List<Estudiante> usuario(Usuario usuario);
}

