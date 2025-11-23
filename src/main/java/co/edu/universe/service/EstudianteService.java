package co.edu.universe.service;

import co.edu.universe.model.*;
import co.edu.universe.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;
    private final UsuarioService usuarioService;
    private final CarreraRepository carreraRepository;
    private final RequisitoInglesRepository reqRepository;

    @Transactional
    public Estudiante crearEstudiante(
            String nombre,
            String correo,
            Long carreraId,
            String nombreUsuario,
            Nivel nivelIngles,
            String examenIngles
    ) {
        Usuario usuario = usuarioService.crearUsuarioConRol(nombreUsuario, "ESTUDIANTE");

        Carrera carrera = carreraRepository.findById(carreraId)
                .orElseThrow();

        RequisitoIngles req = new RequisitoIngles();
        req.setNivel(nivelIngles);
        req.setExamen(examenIngles);
        req.setCompletado(nivelIngles.compareTo(Nivel.B2) >= 0);
        req = reqRepository.save(req);

        Horario horario = new Horario();
        horario = horarioRepository.save(horario);

        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setCorreo(correo);
        estudiante.setCarrera(carrera);
        estudiante.setRequisitoIngles(req);
        estudiante.setHorario(horario);

        estudiante.setUsuario(usuario);
        usuario.setEstudiante(estudiante);

        // GUARDAR ESTUDIANTE ANTES DEL USUARIO
        estudiante = estudianteRepository.save(estudiante);
        estudianteRepository.flush(); // <---- ESTA LÍNEA ES LA SALVACIÓN

        usuarioRepository.save(usuario);

        return estudiante;
    }
    public Estudiante obtenerEstudiante(Long id) {
        return estudianteRepository.obtenerEstudianteCompleto(id)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
    }

    public List<Estudiante> listarEstudiantes() {
        return estudianteRepository.findAll();
    }

    public void eliminarEstudiante(Long id) {
        estudianteRepository.deleteById(id);
    }
}
