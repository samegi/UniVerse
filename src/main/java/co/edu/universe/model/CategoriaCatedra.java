package co.edu.universe.model;
import lombok.Getter;

@Getter
public enum CategoriaCatedra {
    A(15000),
    B(18000),
    C(21000),
    D(25000),
    E(30000);

    private final int valorHora;

    CategoriaCatedra(int valorHora) {
        this.valorHora = valorHora;
    }
}