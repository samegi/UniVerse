package co.edu.universe.repository;

import co.edu.universe.model.Salon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalonRepository extends JpaRepository<Salon, Long> {
    boolean existsByNombre(String nombre);
}
