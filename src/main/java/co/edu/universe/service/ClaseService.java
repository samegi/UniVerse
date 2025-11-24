package co.edu.universe.service;

import co.edu.universe.model.*;
import co.edu.universe.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClaseService {

    private final SemestreService semestreService;
    private final AsignacionService asignacionService;
    private final AsignacionRepository repoAsignacion;
    private final AsignaturaRepository repo;
    private final ClaseRepository repoClase;
    private final SalonService salonService;
    private final EstudianteRepository repoEstudiante;
    private final SalonRepository salonRepository;
    private final ProfesorRepository repoProfesor;
    private final HorarioRepository repoHorario;

    public ClaseService(
            SemestreService semestreService,
            AsignacionService asignacionService,
            AsignacionRepository repoAsignacion,
            AsignaturaRepository repo,
            ClaseRepository repoClase,
            SalonService salonService,
            EstudianteRepository repoEstudiante,
            SalonRepository salonRepository,
            ProfesorRepository repoProfesor,
            HorarioRepository repoHorario
    ) {
        this.semestreService = semestreService;
        this.asignacionService = asignacionService;
        this.repoAsignacion = repoAsignacion;
        this.repo = repo;
        this.repoClase = repoClase;
        this.salonService = salonService;
        this.repoEstudiante = repoEstudiante;
        this.salonRepository = salonRepository;
        this.repoProfesor = repoProfesor;
        this.repoHorario = repoHorario;
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
    public List<Clase> cargarClasesDesdeJson(InputStream jsonStream, Usuario usuario) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            List<Map<String, Object>> clasesJson = mapper.readValue(
                    jsonStream,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<Clase> clasesCreadas = new ArrayList<>();

            for (Map<String, Object> item : clasesJson) {
                Clase clase = new Clase();

                // -------------------------------
                //       DIA DE LA SEMANA
                // -------------------------------
                if (!item.containsKey("dia")) {
                    throw new IllegalArgumentException("Cada clase debe incluir 'dia'.");
                }
                clase.setDia(DiaSemana.valueOf(item.get("dia").toString()));

                // -------------------------------
                //      HORARIOS
                // -------------------------------
                if (!item.containsKey("horaInicio") || !item.containsKey("horaFin")) {
                    throw new IllegalArgumentException("Cada clase debe incluir 'horaInicio' y 'horaFin'.");
                }
                clase.setHoraInicio(LocalTime.parse(item.get("horaInicio").toString()));
                clase.setHoraFin(LocalTime.parse(item.get("horaFin").toString()));

                // -------------------------------
                //        SEMESTRE
                // -------------------------------
                if (!item.containsKey("semestreId")) {
                    throw new IllegalArgumentException("Cada clase debe incluir 'semestreId'.");
                }
                Long semestreId = Long.valueOf(item.get("semestreId").toString());
                Semestre semestre = semestreService.buscarSemestrePorId(semestreId);
                clase.setSemestre(semestre);

                // Validar fecha límite
                validarCreacionDeClase(clase);

                // -------------------------------
                //       ASIGNATURA
                // -------------------------------
                if (!item.containsKey("asignaturaId")) {
                    throw new IllegalArgumentException("Cada clase debe incluir 'asignaturaId'.");
                }
                Long asignaturaId = Long.valueOf(item.get("asignaturaId").toString());
                Asignatura asignatura = repo.findById(asignaturaId)
                        .orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada: " + asignaturaId));
                clase.setAsignatura(asignatura);

                // -------------------------------
                //        SALON
                // -------------------------------
                if (!item.containsKey("salonId")) {
                    throw new IllegalArgumentException("Cada clase debe incluir 'salonId'.");
                }
                Long salonId = Long.valueOf(item.get("salonId").toString());
                Salon salon = salonRepository.findById(salonId)
                        .orElseThrow(() -> new IllegalArgumentException("Salón no encontrado: " + salonId));
                clase.setSalon(salon);

                // -------------------------------
                //      GUARDAR CLASE
                // -------------------------------
                Clase creada = repoClase.save(clase);

                // -------------------------------
                //      PROFESOR (Asignación)
                // -------------------------------
                if (!item.containsKey("profesorId"))
                    throw new IllegalArgumentException("Cada clase debe incluir 'profesorId'.");

                Long profesorId = Long.valueOf(item.get("profesorId").toString());
                Profesor profesor = repoProfesor.findById(profesorId)
                        .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado"));

                Asignacion asignacion = new Asignacion();
                asignacion.setClase(creada);
                asignacion.setProfesor(profesor);

                repoAsignacion.save(asignacion);

                // Agregar lista a clase si la quieres reflejada en el objeto
                if (creada.getAsignaciones() == null) {
                    creada.setAsignaciones(new ArrayList<>());
                }
                creada.getAsignaciones().add(asignacion);

                // Agregar la clase creada a la lista (solo una vez)
                clasesCreadas.add(creada);
            }

            return clasesCreadas;

        } catch (IOException e) {
            System.err.println("=== ERROR AL LEER JSON ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=========================");
            throw new RuntimeException("Error leyendo JSON: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            System.err.println("=== ERROR DE VALIDACIÓN EN JSON ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===================================");
            throw e; // Re-lanzar para que el controlador lo capture
        } catch (Exception e) {
            System.err.println("=== ERROR INESPERADO AL CARGAR CLASES ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=========================================");
            throw new RuntimeException("Error inesperado al cargar clases desde JSON: " + e.getMessage(), e);
        }
    }


    @Transactional
    public Clase updateClase(Long id, Clase updatedClase) {
        validarCreacionDeClase(updatedClase);
        
        // Configurar el ID en updatedClase para que la validación excluya esta clase
        updatedClase.setId(id);
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

        // Actualizar profesores: agregar los nuevos que no estén ya asignados
        if (updatedClase.getProfesoresTemporales() != null && !updatedClase.getProfesoresTemporales().isEmpty()) {
            // Obtener profesores actualmente asignados
            List<Asignacion> asignacionesActuales = repoAsignacion.findByClase(claseActualizada);
            Set<Long> profesoresActualesIds = asignacionesActuales.stream()
                    .map(a -> a.getProfesor().getId())
                    .collect(Collectors.toSet());
            
            // Agregar solo los profesores nuevos que no estén ya asignados
            for (Profesor profesor : updatedClase.getProfesoresTemporales()) {
                if (profesor.getId() == null) {
                    throw new IllegalArgumentException("Cada profesor debe tener un ID válido");
                }
                if (!profesoresActualesIds.contains(profesor.getId())) {
                    asignacionService.crearAsignacion(profesor, claseActualizada);
                }
            }
        }

        asignacionService.actualizarNocturnidadDeClase(claseActualizada);

        return claseActualizada;
    }
    public Map<Clase, Integer> obtenerCantidadEstudiantesPorClase() {

        Map<Clase, Integer> resultado = new HashMap<>();

        List<Object[]> datos = repoEstudiante.countEstudiantesPorClase();
        System.out.println("datos "+ datos);

        for (Object[] fila : datos) {
            Long claseId = (Long) fila[0];
            Integer count = Math.toIntExact((Long) fila[1]);

            Clase clase = repoClase.findById(claseId).orElse(null);
            if (clase != null) {
                resultado.put(clase, count);
            }
        }

        return resultado;
    }

    @Transactional
    public void eliminarClase(Long id) {
        Clase clase = repoClase.findById(id).orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        
        // Primero eliminar todas las asignaciones relacionadas
        List<Asignacion> asignaciones = repoAsignacion.findByClase(clase);
        repoAsignacion.deleteAll(asignaciones);
        
        // Luego eliminar la clase
        repoClase.delete(clase);
    }

    @Transactional
    public List<String> eliminarClaseYRetirarEstudiantes(Long claseId) {

        Clase clase = repoClase.findById(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada."));

        List<String> reporte = new ArrayList<>();

        // 1) Encontrar TODOS los horarios que contienen la clase (no solo los de estudiantes)
        List<Horario> horariosConClase = repoHorario.findByClases_Id(claseId);

        for (Horario horario : horariosConClase) {

            boolean removida = horario.getClases()
                    .removeIf(c -> c.getId().equals(claseId));

            if (removida) {
                // Si tu Horario tiene referencia a estudiante (opcional)
                if (horario.getEstudiante() != null) {
                    reporte.add("Estudiante: " + horario.getEstudiante().getNombre() +
                            " retirado de Asignatura: " + clase.getAsignatura().getNombre() +
                            " | Clase #" + clase.getId());
                }

                repoHorario.save(horario);  // importante: dueño del ManyToMany
            }
        }

        repoHorario.flush(); // asegura que el join table se actualice antes de borrar

        // 2) Borrar asignaciones de profesores (igual que en eliminarClase)
        List<Asignacion> asignaciones = repoAsignacion.findByClase(clase);
        repoAsignacion.deleteAll(asignaciones);

        // 3) Limpiar lado inverso (por consistencia)
        clase.getHorarios().clear();

        // 4) Ahora sí eliminar la clase
        repoClase.delete(clase);

        return reporte;
    }

    // ---------------------------------------------------------------
    // CONSULTAS
    // ---------------------------------------------------------------

    @Transactional(readOnly = true)
    public Clase buscarClasePorId(Long id){
        Clase clase = repoClase.findByIdWithRelations(id)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        
        // Forzar inicialización de asignaciones y profesores (aunque sean EAGER, 
        // asegurarnos de que estén cargados dentro de la transacción)
        if (clase.getAsignaciones() != null) {
            clase.getAsignaciones().forEach(asignacion -> {
                if (asignacion.getProfesor() != null) {
                    // Acceder al profesor para forzar su carga
                    asignacion.getProfesor().getNombre();
                }
            });
        }
        
        return clase;
    }

    @Transactional(readOnly = true)
    public List<Clase> listarClases() {
        return repoClase.findAllWithRelations();
    }

    public List<Clase> listarPorAsignatura(Long asignaturaId) {
        return repoClase.findByAsignaturaId(asignaturaId);
    }
}
