package co.edu.universe.service;

import co.edu.universe.model.*;
import co.edu.universe.repository.AsignacionRepository;
import co.edu.universe.repository.AsignaturaRepository;
import co.edu.universe.repository.ClaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

@Service
public class ClaseService {

    private final SemestreService semestreService;
    private final AsignacionService asignacionService;
    private final AsignacionRepository repoAsignacion;
    private final AsignaturaRepository repo;
    private final ClaseRepository repoClase;
    private final SalonService salonService;

    public ClaseService(
            SemestreService semestreService,
            AsignacionService asignacionService,
            AsignacionRepository repoAsignacion,
            AsignaturaRepository repo,
            ClaseRepository repoClase,
            SalonService salonService
    ) {
        this.semestreService = semestreService;
        this.asignacionService = asignacionService;
        this.repoAsignacion = repoAsignacion;
        this.repo = repo;
        this.repoClase = repoClase;
        this.salonService = salonService;
    }

    // ---------------------------------------------------------------
    // VALIDACIONES
    // ---------------------------------------------------------------

    private void validarDisponibilidadSalon(Clase clase) {
        if (clase.getSalon() == null || clase.getSalon().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un salón");
        }

        boolean ocupado = repoClase.existeChoqueDeHorario(
                clase.getSalon().getId(),
                clase.getDia(),
                clase.getHoraInicio(),
                clase.getHoraFin(),
                clase.getId() == null ? -1L : clase.getId()
        );

        if (ocupado) {
            throw new IllegalArgumentException(
                    "El salón " + clase.getSalon().getNombre() +
                            " ya está ocupado ese día y hora"
            );
        }
    }

    private void validarCreacionDeClase(Clase clase) {
        Instant ahora = Instant.now();

        if (clase.getSemestre() == null || clase.getSemestre().getId() == null) {
            throw new IllegalArgumentException("Debe especificar un semestre para crear la clase");
        }

        Semestre semestre = semestreService.buscarSemestrePorId(clase.getSemestre().getId());
        if (semestre == null) {
            throw new IllegalArgumentException("El semestre especificado no existe");
        }

        Instant fechaMax = semestreService.calcularFechaMaximaCreacionAsignaturas(semestre);
        if (ahora.isAfter(fechaMax)) {
            throw new IllegalStateException(
                    "No se pueden crear clases para el semestre " + semestre.getNombre() +
                            " porque ya pasó la fecha límite (" + fechaMax + ")"
            );
        }
    }

    // ---------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------

    @Transactional
    public Clase crearClase(Clase clase) {
        validarCreacionDeClase(clase);
        validarDisponibilidadSalon(clase);

        Clase claseGuardada = repoClase.save(clase);

        if (clase.getProfesoresTemporales() != null && !clase.getProfesoresTemporales().isEmpty()) {
            for (Profesor profesor : clase.getProfesoresTemporales()) {
                if (profesor.getId() == null) {
                    throw new IllegalArgumentException("Cada profesor debe tener un ID válido");
                }
                asignacionService.crearAsignacion(profesor, claseGuardada);
            }
        }

        return claseGuardada;
    }

    @Transactional
    public Clase updateClase(Long id, Clase updatedClase) {
        validarCreacionDeClase(updatedClase);
        validarDisponibilidadSalon(updatedClase);

        Clase claseActualizada = repoClase.findById(id)
                .map(clase -> {
                    clase.setDia(updatedClase.getDia());
                    clase.setHoraInicio(updatedClase.getHoraInicio());
                    clase.setHoraFin(updatedClase.getHoraFin());
                    clase.setSemestre(updatedClase.getSemestre());
                    clase.setAsignatura(updatedClase.getAsignatura());
                    clase.setSalon(updatedClase.getSalon());
                    return repoClase.save(clase);
                })
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        asignacionService.actualizarNocturnidadDeClase(claseActualizada);

        return claseActualizada;
    }

    @Transactional
    public void eliminarClase(Long id) {
        Clase clase = repoClase.findById(id).orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        repoClase.delete(clase);
    }

    // ---------------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------------

    public Clase buscarClasePorId(Long id){
        return repoClase.findById(id).orElseThrow(() -> new RuntimeException("Clase no encontrada"));
    }

    public List<Clase> listarClases() {
        return repoClase.findAll();
    }

    public List<Clase> listarPorAsignatura(Long asignaturaId) {
        return repoClase.findByAsignaturaId(asignaturaId);
    }
}
