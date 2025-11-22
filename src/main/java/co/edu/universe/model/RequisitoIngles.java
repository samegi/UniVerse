package co.edu.universe.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class RequisitoIngles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Nivel nivel; // A1, A2, B1, B2, C1, C2

    private String examen; // Ejemplo: TOEFL, IELTS, etc.

    private boolean completado; // true si nivel >= B2

    @OneToOne(mappedBy="requisitoIngles")
    @ToString.Exclude
    private Estudiante estudiante;
}

