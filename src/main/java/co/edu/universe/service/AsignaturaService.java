package co.edu.universe.service;

import co.edu.universe.model.Asignatura;
import co.edu.universe.model.Semestre;
import co.edu.universe.model.Usuario;
import co.edu.universe.repository.AsignaturaRepository;
import co.edu.universe.repository.ClaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final ClaseRepository claseRepository;

    @Autowired
    private SemestreService semestreService;

    // -------------------------------------------------------------------------------------
    //                                VALIDACIONES
    // -------------------------------------------------------------------------------------

    private void validarFechaLimite(Semestre semestre) {
        Instant fechaMaxima = semestreService.calcularFechaMaximaCreacionAsignaturas(semestre);
        Instant ahora = Instant.now();

        if (ahora.isAfter(fechaMaxima)) {
            throw new IllegalStateException(
                    "No se pueden gestionar asignaturas después de la fecha límite: " + fechaMaxima
            );
        }
    }

    private void validarDatos(Asignatura asignatura) {
        if (asignatura.getSemestre() == null) {
            throw new IllegalArgumentException("La asignatura debe tener un semestre asociado.");
        }

        /*if (asignatura.getCarrera() == null) {
            throw new IllegalArgumentException("La asignatura debe tener una carrera asociada.");
        }*/

        if (asignatura.getNombre() == null || asignatura.getNombre().isBlank()) {
            throw new IllegalArgumentException("La asignatura debe tener nombre.");
        }
    }

    // -------------------------------------------------------------------------------------
    //                                   CRUD
    // -------------------------------------------------------------------------------------

    public List<Asignatura> listarAsignaturas() {
        return asignaturaRepository.findAll();
    }

    public Asignatura buscarPorId(Long id) {
        return asignaturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada."));
    }

    // -------------------- CREAR --------------------

    public Asignatura crearAsignatura(Asignatura asignatura, Usuario usuario) {
        validarDatos(asignatura);

        Semestre semestre = asignatura.getSemestre();
        validarFechaLimite(semestre);

        return asignaturaRepository.save(asignatura);
    }
    @Transactional
    public List<Asignatura> cargarAsignaturasDesdeJson(InputStream jsonStream, Usuario usuario) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Convertir JSON a lista de mapas
            List<Map<String, Object>> asignaturasJson = mapper.readValue(
                    jsonStream,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<Asignatura> asignaturasCreadas = new ArrayList<>();

            for (Map<String, Object> item : asignaturasJson) {

                Asignatura asignatura = new Asignatura();

                asignatura.setNombre((String) item.get("nombre"));
                asignatura.setCreditos((Integer) item.get("creditos"));
                asignatura.setIngles(Boolean.TRUE.equals(item.get("ingles")));

                // -------------------------------
                //     RESOLVER SEMESTRE
                // -------------------------------
                if (!item.containsKey("semestreId")) {
                    throw new IllegalArgumentException("Cada asignatura debe incluir 'semestreId'.");
                }

                Long semestreId = Long.valueOf(item.get("semestreId").toString());
                Semestre semestre = semestreService.buscarSemestrePorId(semestreId);
                asignatura.setSemestre(semestre);

                // Validaciones ya definidas
                validarDatos(asignatura);
                validarFechaLimite(semestre);

                // Guardar
                Asignatura creada = asignaturaRepository.save(asignatura);
                asignaturasCreadas.add(creada);
            }

            return asignaturasCreadas;

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo JSON: " + e.getMessage(), e);
        }
    }

    // -------------------- ACTUALIZAR --------------------

    public Asignatura actualizarAsignatura(Long id, Asignatura datos, Usuario usuario) {
        validarDatos(datos);

        Asignatura existente = buscarPorId(id);

        Semestre semestre = datos.getSemestre();
        validarFechaLimite(semestre);

        existente.setNombre(datos.getNombre());
        existente.setCreditos(datos.getCreditos());
        existente.setIngles(datos.isIngles());
        existente.setSemestre(datos.getSemestre());
        existente.setCarrera(datos.getCarrera());

        return asignaturaRepository.save(existente);
    }

    // -------------------- ELIMINAR --------------------

    @Transactional
    public void eliminarAsignatura(Long id, Usuario usuario) {
        Asignatura asignatura = buscarPorId(id);

        // Romper relaciones antes de eliminar
        asignatura.getClases().clear();
        asignatura.setCarrera(null);
        asignatura.setSemestre(null);

        asignaturaRepository.delete(asignatura);
    }
}
