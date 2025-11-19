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

import java.time.Instant;
import java.util.List;

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

    private void validarPermisoDirectorCarrera(Usuario usuario) {
        if (!usuario.getRol().getNombre().equalsIgnoreCase("DirectorCarrera")) {
            throw new SecurityException("Solo un Director de Carrera puede gestionar asignaturas.");
        }
    }

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

        validarPermisoDirectorCarrera(usuario);
        validarDatos(asignatura);

        Semestre semestre = asignatura.getSemestre();
        validarFechaLimite(semestre);

        return asignaturaRepository.save(asignatura);
    }

    // -------------------- ACTUALIZAR --------------------

    public Asignatura actualizarAsignatura(Long id, Asignatura datos, Usuario usuario) {

        validarPermisoDirectorCarrera(usuario);
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

        validarPermisoDirectorCarrera(usuario);

        Asignatura asignatura = buscarPorId(id);

        // Romper relaciones antes de eliminar
        asignatura.getClases().clear();
        asignatura.setCarrera(null);
        asignatura.setSemestre(null);

        asignaturaRepository.delete(asignatura);
    }
}
