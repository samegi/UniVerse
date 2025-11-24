package co.edu.universe.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String correo;
    @Enumerated(EnumType.STRING)
    private TipoProfesor tipoProfesor; // PLANTA o CATEDRA

    // ---- Atributos para planta ----
    private int minHoras;
    private int maxHoras;
    private double valorHora;
    private double pagoExtraPorHora; // $50,000 por hora extra

    // ---- Atributos para cátedra ----
    private String empresa;
    @Enumerated(EnumType.STRING)
    private CategoriaCatedra categoria;

    @OneToMany(mappedBy = "profesor", fetch = FetchType.EAGER)
    private List<Asignacion> asignaciones;

    @ManyToOne
    private Departamento departamento;

    @Override
    public String toString() {
        return nombre;
    }

}
