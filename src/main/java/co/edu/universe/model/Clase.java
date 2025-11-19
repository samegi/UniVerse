package co.edu.universe.model;

import java.time.LocalTime;
import java.util.*;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    @ManyToOne
    private Semestre semestre;

    @ManyToOne
    private Asignatura asignatura;

    @OneToMany(mappedBy = "clase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;
    @Transient // no se guarda en BD
    private List<Profesor> profesoresTemporales = new ArrayList<>();

}

