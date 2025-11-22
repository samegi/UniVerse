package co.edu.universe.service;

import co.edu.universe.model.Clase;
import co.edu.universe.model.Horario;
import co.edu.universe.model.Semestre;
import co.edu.universe.repository.ClaseRepository;
import co.edu.universe.repository.HorarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepository;
    private final ClaseRepository claseRepository;
    private final SemestreService semestreService;

    @Transactional
    public void agregarClase(Long horarioId, Long claseId) {

        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        Semestre semestre = clase.getSemestre();
        if (semestre == null) {
            throw new RuntimeException("La clase no tiene un semestre asociado");
        }

        // 1. Validar ventana de inscripción
        Instant ahora = Instant.now();
        Instant inicioInscripciones = semestreService.calcularFechaInicioInscripciones(semestre);
        Instant finInscripciones = semestreService.calcularFechaFinInscripciones(semestre);

        if (ahora.isBefore(inicioInscripciones) || ahora.isAfter(finInscripciones)) {
            throw new RuntimeException(
                    "No estás en el período de inscripciones. " +
                            "Las inscripciones van del " + inicioInscripciones +
                            " al " + finInscripciones + "."
            );
        }

        // 2. Validar choque de horario
        for (Clase c : horario.getClases()) {
            if (hayChoque(c, clase)) {
                throw new RuntimeException(
                        "Choque de horario con la clase: " + c.getAsignatura().getNombre()
                );
            }
        }

        // 3. Agregar
        horario.getClases().add(clase);
        horarioRepository.save(horario);
    }


    public boolean hayChoque(Clase c1, Clase c2) {
        return c1.getDia().equals(c2.getDia()) &&
                c1.getHoraInicio().isBefore(c2.getHoraFin()) &&
                c2.getHoraInicio().isBefore(c1.getHoraFin());
    }

    @Transactional
    public void eliminarClase(Long horarioId, Long claseId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Clase clase = claseRepository.findById(claseId)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        horario.getClases().remove(clase);
        horarioRepository.save(horario);
    }

    public List<Clase> obtenerClases(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        return horario.getClases();
    }
}
