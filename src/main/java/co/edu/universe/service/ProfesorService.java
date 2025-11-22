package co.edu.universe.service;

import co.edu.universe.model.Asignacion;
import co.edu.universe.model.Profesor;
import co.edu.universe.model.TipoProfesor;
import co.edu.universe.repository.ProfesorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfesorService {

    private final AsignacionService asignacionService;
    private final ClaseService claseService;
    private final ProfesorRepository repoProfesor;

    public ProfesorService(AsignacionService asignacionService,
                           ProfesorRepository repoProfesor, ClaseService claseService) {
        this.asignacionService = asignacionService;
        this.repoProfesor = repoProfesor;
        this.claseService = claseService;
    }

    // =====================================================
    // MÉTODOS DE CÁLCULO
    // =====================================================

    /**
     * Calcula el total de horas dictadas por un profesor (según sus asignaciones).
     */
    public int calcularTotalHorasDictadas(Profesor profesor) {
        return profesor.getAsignaciones().stream()
                .mapToInt(Asignacion::getHoras)
                .sum();
    }

    /**
     * Calcula el total de horas nocturnas dictadas por un profesor.
     */
    public int calcularTotalHorasNocturnas(Profesor profesor) {
        return profesor.getAsignaciones().stream()
                .filter(Asignacion::isNocturna)
                .mapToInt(Asignacion::getHoras)
                .sum();
    }

    /**
     * Calcula el sobresueldo por clases nocturnas (35% adicional sobre el valor hora).
     * Aplica tanto a profesores de planta como de cátedra.
     */
    public double calcularSobresueldoNocturno(Profesor profesor) {
        double valorHoraBase = (profesor.getTipoProfesor() == TipoProfesor.CATEDRA)
                ? profesor.getCategoria().getValorHora()
                : profesor.getValorHora();

        int horasNocturnas = calcularTotalHorasNocturnas(profesor);
        return horasNocturnas * valorHoraBase * 0.35;
    }
    // =====================================================
    // MÉTODOS DE APOYO
    // =====================================================

    /**
     * Calcula el salario total de un profesor teniendo en cuenta horas, extras y nocturnas.
     */
    public double calcularSalarioTotal(Profesor profesor) {
        int horasTotales = calcularTotalHorasDictadas(profesor);
        double salarioBase = 0.0;

        if (profesor.getTipoProfesor() == TipoProfesor.PLANTA) {
            salarioBase = horasTotales * profesor.getValorHora();
            if (horasTotales > profesor.getMaxHoras()) {
                int horasExtra = horasTotales - profesor.getMaxHoras();
                salarioBase += horasExtra * profesor.getPagoExtraPorHora();
            }
        } else { // Cátedra
            if (horasTotales > 19) {
                throw new IllegalArgumentException("Profesor de cátedra no puede exceder 19 horas semanales");
            }
            salarioBase = horasTotales * profesor.getCategoria().getValorHora();
        }

        // Sumar sobresueldo nocturno
        return salarioBase + calcularSobresueldoNocturno(profesor);
    }
    // =====================================================
    // CRUD Profesor
    // =====================================================
    public List<Profesor> listarProfesores() {
        return repoProfesor.findAll();
    }

}
