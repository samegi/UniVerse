package co.edu.universe.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Un horario tiene muchas clases
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "horario_clase",
            joinColumns = @JoinColumn(name = "horario_id"),
            inverseJoinColumns = @JoinColumn(name = "clase_id")
    )
    private List<Clase> clases = new ArrayList<>();


    @OneToOne(mappedBy = "horario")
    private Estudiante estudiante;
}
