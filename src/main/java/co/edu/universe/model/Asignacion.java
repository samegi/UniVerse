package co.edu.universe.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @ManyToOne
    @JoinColumn(name = "clase_id")
    private Clase clase;

    private int horas; // cuántas horas dicta ese profesor en esa clase
    private boolean nocturna; // si la clase es después de las 6pm
}