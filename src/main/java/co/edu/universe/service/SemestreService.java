package co.edu.universe.service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import co.edu.universe.model.Asignatura;
import co.edu.universe.model.Clase;
import co.edu.universe.model.Semestre;
import co.edu.universe.model.Usuario;
import co.edu.universe.repository.SemestreRepository;
import co.edu.universe.utils.Sesion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SemestreService {

    @Autowired
    private SemestreRepository semestreRepository;

    private void validarPermisoDirectorCarrera(Usuario usuario) {
        if (!usuario.getRol().getNombre().equalsIgnoreCase("DirectorCarrera")) {
            throw new SecurityException("No tienes permisos para gestionar semestres");
        }
    }

    // =====================
    //     CRUD BÁSICO
    // =====================

    /**
     * Crear semestre
     */
    public Semestre crearSemestre(Semestre semestre, Usuario usuario) {
        validarPermisoDirectorCarrera(Sesion.getUsuario());
        validarFechas(semestre);
        return semestreRepository.save(semestre);
    }
    /**
     * Obtener semestre por ID
     */
    public Semestre buscarSemestrePorId(Long id) {
        return semestreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semestre no encontrado con id: " + id));
    }

    /**
     * Listar todos los semestres
     */
    public List<Semestre> listarSemestre() {
        return semestreRepository.findAll();
    }

    /**
     * Actualizar semestre
     */
    public Semestre actualizarSemestre(Long id, Semestre actualizado, Usuario usuario) {
        validarPermisoDirectorCarrera(Sesion.getUsuario());
        Semestre existente = buscarSemestrePorId(id);

        existente.setNombre(actualizado.getNombre());
        existente.setAño(actualizado.getAño());
        existente.setPeriodo(actualizado.getPeriodo());
        existente.setFechaInicio(actualizado.getFechaInicio());
        existente.setFechaFin(actualizado.getFechaFin());

        validarFechas(existente);

        return semestreRepository.save(existente);
    }


    /**
     * Eliminar semestre
     * PENDIENTE Para eliminar un semestre no deben haber asignaturas, ni clases asociadas
     *
     */
    public void eliminarSemestre(Long id, Usuario usuario) {
        validarPermisoDirectorCarrera(Sesion.getUsuario());
        Semestre semestre = buscarSemestrePorId(id);
        semestreRepository.delete(semestre);
    }

    // ======================================================
    //     VALIDACIÓN DE FECHAS
    // ======================================================
    private void validarFechas(Semestre semestre) {
        if (semestre.getFechaInicio() == null || semestre.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }

        if (!semestre.getFechaFin().isAfter(semestre.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }
    }

    // ==================================================================
    //   REGLAS DE NEGOCIO: INSCRIPCIONES Y CREACIÓN DE ASIGNATURAS/CLASES
    // ==================================================================

    /**
     * Tres meses antes del inicio
     */
    public Instant calcularFechaMaximaCreacionAsignaturas(Semestre semestre) {
        if (semestre.getFechaInicio() == null) {
            throw new IllegalArgumentException("El semestre no tiene fecha de inicio definida");
        }
        return semestre.getFechaInicio().minus(90, ChronoUnit.DAYS);
    }

    /**
     * Una semana antes del inicio
     */
    public Instant calcularFechaInicioInscripciones(Semestre semestre) {
        if (semestre.getFechaInicio() == null) {
            throw new IllegalArgumentException("El semestre no tiene fecha de inicio definida");
        }
        return semestre.getFechaInicio().minus(7, ChronoUnit.DAYS);
    }

    /**
     * Un día antes del inicio
     */
    public Instant calcularFechaFinInscripciones(Semestre semestre) {
        if (semestre.getFechaInicio() == null) {
            throw new IllegalArgumentException("El semestre no tiene fecha de inicio definida");
        }
        return semestre.getFechaInicio().minus(1, ChronoUnit.DAYS);
    }

    /**
     * Validación
     */
    public boolean puedeRegistrarAsignaturas(Semestre semestre, Instant fechaActual) {
        return fechaActual.isBefore(calcularFechaMaximaCreacionAsignaturas(semestre));
    }

    public boolean inscripcionesAbiertas(Semestre semestre, Instant fechaActual) {
        Instant inicio = calcularFechaInicioInscripciones(semestre);
        Instant fin = calcularFechaFinInscripciones(semestre);
        return !fechaActual.isBefore(inicio) && !fechaActual.isAfter(fin);
    }

    public List<Clase> obtenerClases(Long idSemestre) {
        Semestre semestre = buscarSemestrePorId(idSemestre);
        return semestre.getClases();
    }

    public List<Clase> obtenerAsignaturas(Long idSemestre) {
        Semestre semestre = buscarSemestrePorId(idSemestre);
        return semestre.getAsignaturas();
    }
    public boolean existeNombre(String nombre, Long idExcluir) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        return semestreRepository.findAll().stream()
                .anyMatch(s ->
                        s.getNombre().equalsIgnoreCase(nombre.trim()) &&
                                (idExcluir == null || !s.getId().equals(idExcluir))
                );
    }

    public boolean existeFechaInicio(Instant fecha, Long idExcluir) {
        if (fecha == null) {
            return false;
        }

        return semestreRepository.findAll().stream()
                .anyMatch(s ->
                        s.getFechaInicio().equals(fecha) &&
                                (idExcluir == null || !s.getId().equals(idExcluir))
                );
    }

    public boolean existeFechaFin(Instant fecha, Long idExcluir) {
        if (fecha == null) {
            return false;
        }

        return semestreRepository.findAll().stream()
                .anyMatch(s ->
                        s.getFechaFin().equals(fecha) &&
                                (idExcluir == null || !s.getId().equals(idExcluir))
                );
    }

}