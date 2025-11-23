package co.edu.universe.model;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiaSemana dia;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id")
    @ToString.Exclude
    private Semestre semestre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asignatura_id")
    @ToString.Exclude
    private Asignatura asignatura;

    @OneToMany(mappedBy = "clase", fetch = FetchType.EAGER)
    private List<Asignacion> asignaciones = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @Transient
    private List<Profesor> profesoresTemporales = new ArrayList<>();

    @ManyToMany(mappedBy = "clases")
    @ToString.Exclude
    private List<Horario> horarios = new ArrayList<>();

    @Transient
    public String getProfesorNombre() {
        if (asignaciones == null || asignaciones.isEmpty()) return "";
        return asignaciones.stream()
                .map(a -> a.getProfesor().getNombre())
                .distinct()
                .collect(Collectors.joining(", "));
    }
}


