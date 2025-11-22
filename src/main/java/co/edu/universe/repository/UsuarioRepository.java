package co.edu.universe.repository;

import co.edu.universe.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombre(String nombre);

    boolean existsByNombre(String nombre);   //

    List<Usuario> findAllByNombre(String nombre);  //
}

