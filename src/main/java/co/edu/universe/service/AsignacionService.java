package co.edu.universe.service;

import co.edu.universe.model.Asignacion;
import co.edu.universe.model.Clase;
import co.edu.universe.model.Profesor;
import co.edu.universe.model.TipoProfesor;
import co.edu.universe.repository.AsignacionRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import java.time.*;

@Service
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;

    public AsignacionService(AsignacionRepository asignacionRepository) {
        this.asignacionRepository = asignacionRepository;
    }

    // =====================================================
    // MÉTODOS INTERNOS DE CÁLCULO (sin depender de ClaseService)
    // =====================================================

    private int calcularDuracionHoras(Clase clase) {
        if (clase.getHoraInicio() == null || clase.getHoraFin() == null) {
            return 0;
        }
        long minutos = Duration.between(clase.getHoraInicio(), clase.getHoraFin()).toMinutes();
        return (int) (minutos / 60);
    }

    private boolean esClaseNocturna(Clase clase) {
        if (clase.getHoraInicio() == null) {
            return false;
        }
        return clase.getHoraInicio().isAfter(LocalTime.of(17, 59));
    }

    // =====================================================
    // CRUD Y REGLAS DE NEGOCIO
    // =====================================================

    public Asignacion crearAsignacion(Profesor profesor, Clase clase) {
        // Validar duplicado
        Optional<Asignacion> existente = asignacionRepository.findByProfesorAndClase(profesor, clase);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El profesor ya está asignado a esta clase.");
        }

        // Validar límite de cátedra
        if (profesor.getTipoProfesor() == TipoProfesor.CATEDRA) {
            // Usar el repositorio para obtener las asignaciones del profesor
            // en lugar de acceder a la relación lazy
            List<Asignacion> asignacionesProfesor = asignacionRepository.findByProfesor(profesor);
            int horasActuales = asignacionesProfesor.stream()
                    .mapToInt(a -> calcularDuracionHoras(a.getClase()))
                    .sum();
            int horasNuevaClase = calcularDuracionHoras(clase);
            if (horasActuales + horasNuevaClase > 19) {
                throw new IllegalArgumentException("El profesor de cátedra no puede exceder 19 horas semanales.");
            }
        }
        // Crear asignación
        Asignacion asignacion = new Asignacion();
        asignacion.setProfesor(profesor);
        asignacion.setClase(clase);
        asignacion.setNocturna(esClaseNocturna(clase));
        asignacion.setHoras(calcularDuracionHoras(clase));

        return asignacionRepository.save(asignacion);
    }

    public void eliminarAsignacion(Long id) {
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada"));
        asignacionRepository.delete(asignacion);
    }

    public List<Asignacion> obtenerPorClase(Clase clase) {
        return asignacionRepository.findByClase(clase);
    }

    public void actualizarNocturnidadDeClase(Clase clase) {
        List<Asignacion> asignaciones = asignacionRepository.findByClase(clase);
        for (Asignacion a : asignaciones) {
            a.setNocturna(esClaseNocturna(clase));
            asignacionRepository.save(a);
        }
    }
}
