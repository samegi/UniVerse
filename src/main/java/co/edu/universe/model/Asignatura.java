package co.edu.universe.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int creditos;
    private boolean ingles;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clase> clases = new ArrayList<>();

    @ManyToOne
    private Semestre semestre;

    @ManyToOne
    private Carrera carrera;

    @Override
    public String toString() {
        return nombre;
    }

}
