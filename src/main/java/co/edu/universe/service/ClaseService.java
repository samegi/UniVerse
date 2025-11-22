package co.edu.universe.service;

import co.edu.universe.model.*;
import co.edu.universe.repository.AsignacionRepository;
import co.edu.universe.repository.AsignaturaRepository;
import co.edu.universe.repository.ClaseRepository;
import co.edu.universe.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.*;

@Service
public class ClaseService {

    private final SemestreService semestreService;
    private final AsignacionService asignacionService;
    private final AsignacionRepository repoAsignacion;
    private final AsignaturaRepository repo;
    private final ClaseRepository repoClase;
    private final SalonService salonService;
    private final EstudianteRepository repoEstudiante;

    public ClaseService(
            SemestreService semestreService,
            AsignacionService asignacionService,
            AsignacionRepository repoAsignacion,
            AsignaturaRepository repo,
            ClaseRepository repoClase,
            SalonService salonService,
            EstudianteRepository repoEstudiante
    ) {
        this.semestreService = semestreService;
        this.asignacionService = asignacionService;
        this.repoAsignacion = repoAsignacion;
        this.repo = repo;
        this.repoClase = repoClase;
        this.salonService = salonService;
        this.repoEstudiante = repoEstudiante;
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
    public Map<Clase, Integer> obtenerCantidadEstudiantesPorClase() {

        List<Clase> clases = repoClase.findAll();
        List<Estudiante> estudiantes = repoEstudiante.findAll();

        Map<Clase, Integer> conteo = new HashMap<>();

        for (Clase clase : clases) {
            int count = 0;

            for (Estudiante est : estudiantes) {
                if (est.getHorario() != null && est.getHorario().getClases().contains(clase)) {
                    count++;
                }
            }

            conteo.put(clase, count);
        }

        return conteo;
    }



    @Transactional
    public void eliminarClase(Long id) {
        Clase clase = repoClase.findById(id).orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        repoClase.delete(clase);
    }

    @Transactional
    public List<String> eliminarClaseYRetirarEstudiantes(Long claseId) {

        Clase clase = repoClase.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada."));

        List<Estudiante> estudiantes = repoEstudiante.findAll();

        List<String> reporte = new ArrayList<>();

        for (Estudiante est : estudiantes) {

            if (est.getHorario() == null) continue;

            if (est.getHorario().getClases().remove(clase)) {

                reporte.add(
                        "Estudiante: " + est.getNombre() +
                                " retirado de Asignatura: " + clase.getAsignatura().getNombre() +
                                " | Clase #" + clase.getId()
                );
            }
        }

        // Guardar cambios en horarios
        repoEstudiante.saveAll(estudiantes);

        // Finalmente eliminar la clase
        repoClase.delete(clase);

        return reporte;
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
