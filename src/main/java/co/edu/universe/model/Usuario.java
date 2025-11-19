package co.edu.universe.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String nombre;

    @ManyToOne
    private Rol rol;

    /*@OneToMany
    private List<Asignatura> asignaturasCreadas;*/
}
