package co.edu.universe.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Salon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String nombre;
    private int cupoMax;

    @OneToMany(mappedBy = "salon")
    private List<Clase> clases;
}
