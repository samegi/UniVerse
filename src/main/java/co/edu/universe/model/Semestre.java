package co.edu.universe.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.*;

@Entity
@Data
public class Semestre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant fechaInicio;
    private Instant fechaFin;

    private String nombre;
    private int año;
    private int periodo;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clase> clases = new ArrayList<>();

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clase> asignaturas = new ArrayList<>();

    @Override
    public String toString() {
        return nombre;
    }
}
