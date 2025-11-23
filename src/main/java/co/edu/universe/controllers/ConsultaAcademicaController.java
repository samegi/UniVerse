package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.*;
import co.edu.universe.repository.AsignaturaRepository;
import co.edu.universe.repository.ClaseRepository;
import co.edu.universe.repository.EstudianteRepository;
import co.edu.universe.utils.Paths;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultaAcademicaController {

    private final AsignaturaRepository asignaturaRepo;
    private final ClaseRepository claseRepo;
    private final EstudianteRepository estudianteRepo;

    // --- SECCIÓN A: Estudiantes inscritos en una clase ---
    @FXML private ComboBox<Asignatura> comboAsignatura;
    @FXML private ComboBox<Clase> comboClase;
    @FXML private TableView<Estudiante> tablaEstudiantes;

    @FXML private TableColumn<Estudiante, String> colEstNombre;
    @FXML private TableColumn<Estudiante, String> colEstCorreo;
    @FXML private TableColumn<Estudiante, String> colEstPrograma;

    // --- SECCIÓN B: Asignaturas inscritas por estudiante ---
    @FXML private ComboBox<Estudiante> comboEstudiante;
    @FXML private TableView<Clase> tablaClases;
    @FXML private TableColumn<Clase, String> colAsig;
    @FXML private TableColumn<Clase, String> colProf;
    @FXML private TableColumn<Clase, DiaSemana> colDia;
    @FXML private TableColumn<Clase, String> colInicio;
    @FXML private TableColumn<Clase, String> colFin;

    @FXML
    public void initialize() {

        // INICIALIZAR COMBOS
        comboAsignatura.getItems().addAll(asignaturaRepo.findAll());
        comboEstudiante.getItems().addAll(estudianteRepo.findAll());

        comboAsignatura.setOnAction(e -> cargarClasesPorAsignatura());
        comboClase.setOnAction(e -> cargarEstudiantesPorClase());
        comboEstudiante.setOnAction(e -> cargarClasesDeEstudiante());
        // ========== CONFIG TABLA A ==========
        colEstNombre.setCellValueFactory(
                cell -> new SimpleObjectProperty<>(cell.getValue().getNombre())
        );
        colEstCorreo.setCellValueFactory(
                cell -> new SimpleObjectProperty<>(
                        cell.getValue().getCorreo() != null
                                ? cell.getValue().getCorreo()
                                : "Sin correo"
                )
        );
        colEstPrograma.setCellValueFactory(
                cell -> new SimpleObjectProperty<>(
                        cell.getValue().getCarrera() != null
                                ? cell.getValue().getCarrera().getNombre()
                                : "Sin carrera"
                )
        );


        // CONFIG TABLA B
        colAsig.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getAsignatura().getNombre()));

        colProf.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getProfesorNombre()));

        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));

        colInicio.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getHoraInicio().toString()));

        colFin.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getHoraFin().toString()));
    }

    // =============================
    // SECCIÓN A
    // =============================

    private void cargarClasesPorAsignatura() {
        Asignatura asignatura = comboAsignatura.getValue();
        if (asignatura == null) return;

        List<Clase> clases = claseRepo.findByAsignatura(asignatura);
        comboClase.getItems().setAll(clases);
    }
    private void cargarEstudiantesPorClase() {
        Clase clase = comboClase.getValue();
        if (clase == null) return;

        List<Estudiante> estudiantes = estudianteRepo.findEstudiantesByClase(clase.getId());

        tablaEstudiantes.getItems().setAll(estudiantes);
    }
    // =============================
    // SECCIÓN B
    // =============================

    private void cargarClasesDeEstudiante() {
        Estudiante est = comboEstudiante.getValue();
        if (est == null) return;

        List<Clase> clases = est.getHorario().getClases();
        tablaClases.getItems().setAll(clases);
    }

    @FXML
    public void regresarMenu(ActionEvent event) {
        App.setRoot(Paths.MENU_DIR_CARRERA);
    }
}

