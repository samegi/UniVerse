package co.edu.universe.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Salon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer cupoMax;

    @OneToMany(mappedBy = "salon")
    private List<Clase> clases;

    @Override
    public String toString() {
        return nombre;
    }
}
