package co.edu.universe.repository;
import co.edu.universe.model.RequisitoIngles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitoInglesRepository extends JpaRepository<RequisitoIngles, Long> {
}
