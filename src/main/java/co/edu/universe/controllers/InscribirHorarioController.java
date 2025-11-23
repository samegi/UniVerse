package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.*;
import co.edu.universe.repository.AsignaturaRepository;
import co.edu.universe.repository.ClaseRepository;
import co.edu.universe.service.EstudianteService;
import co.edu.universe.service.HorarioService;
import co.edu.universe.utils.Paths;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InscribirHorarioController {

    private final AsignaturaRepository asignaturaRepository;
    private final ClaseRepository claseRepository;
    private final EstudianteService estudianteService;
    private final HorarioService horarioService;

    private Estudiante estudiante;

    @FXML private ComboBox<Asignatura> comboAsignatura;
    @FXML private TableView<Clase> tablaClases;

    @FXML private TableColumn<Clase, String> colProfesor;
    @FXML private TableColumn<Clase, DiaSemana> colDia;
    @FXML private TableColumn<Clase, LocalTime> colInicio;
    @FXML private TableColumn<Clase, LocalTime> colFin;
    @FXML private TableColumn<Clase, Integer> colCreditos;

    @FXML private ProgressBar barraCreditos;
    @FXML private Label lblCreditos;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {

        // Configurar columnas
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesorNombre"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("horaFin"));

        colCreditos.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getAsignatura().getCreditos())
        );

        comboAsignatura.getItems().addAll(asignaturaRepository.findAll());
        comboAsignatura.setOnAction(e -> cargarClases());

        // ⚠ NO LLAMAR actualizarBarraCreditos() aquí
        // porque el estudiante todavía no existe
    }

    // ============================
    // RECIBIR ESTUDIANTE
    // ============================
    public void setEstudiante(Estudiante estudiante) {
        // Buscar versión actualizada del estudiante
        this.estudiante = estudianteService.obtenerEstudiante(estudiante.getId());
        actualizarBarraCreditos();
    }

    // ============================
    // CARGAR CLASES SEGÚN ASIGNATURA
    // ============================
    private void cargarClases() {
        Asignatura asignatura = comboAsignatura.getValue();
        if (asignatura == null) return;

        List<Clase> clases = claseRepository.findByAsignatura(asignatura);
        tablaClases.getItems().setAll(clases);
    }

    // ============================
    // AGREGAR CLASE AL HORARIO
    // ============================
    @FXML
    public void agregarClase() {
        try {
            Clase clase = tablaClases.getSelectionModel().getSelectedItem();

            if (clase == null) {
                lblMensaje.setStyle("-fx-text-fill:red;");
                lblMensaje.setText("Seleccione una clase.");
                return;
            }

            horarioService.agregarClase(
                    estudiante.getHorario().getId(),
                    clase.getId()
            );

            // Refrescar desde la BD
            this.estudiante = estudianteService.obtenerEstudiante(estudiante.getId());

            lblMensaje.setStyle("-fx-text-fill:green;");
            lblMensaje.setText("Clase agregada exitosamente.");

            actualizarBarraCreditos();

        } catch (Exception ex) {
            lblMensaje.setStyle("-fx-text-fill:red;");
            lblMensaje.setText(ex.getMessage());
        }
    }

    // ============================
    // CÁLCULO DE CRÉDITOS
    // ============================
    private int calcularCreditos() {
        return estudiante.getHorario().getClases().stream()
                .mapToInt(c -> c.getAsignatura().getCreditos())
                .sum();
    }

    private void actualizarBarraCreditos() {
        int total = calcularCreditos();
        barraCreditos.setProgress(total / 20.0);
        lblCreditos.setText(total + "/20 créditos");
    }

    @FXML
    void regresarMenu(ActionEvent event) {
        App.setRoot(Paths.MENU_ESTUDIANTE);
    }

    @FXML
    void verHorario(ActionEvent event) {
        App.setRoot(Paths.VER_HORARIO);
    }
}
