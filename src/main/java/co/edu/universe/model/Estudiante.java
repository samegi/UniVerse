package co.edu.universe.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ToString.Include
    private String nombre;

    @ToString.Include
    private String correo;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Carrera carrera;

    @OneToOne(mappedBy = "estudiante")
    @ToString.Exclude
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name="requisito_ingles_id")
    @ToString.Exclude
    private RequisitoIngles requisitoIngles;

    @OneToOne
    @JoinColumn(name="horario_id")
    @ToString.Exclude
    private Horario horario;
}
