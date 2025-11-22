package co.edu.universe.repository;

import co.edu.universe.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol(String nombreRol);
}
