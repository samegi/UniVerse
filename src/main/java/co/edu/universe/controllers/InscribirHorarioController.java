package co.edu.universe.controllers;

import co.edu.universe.model.*;
import co.edu.universe.repository.AsignaturaRepository;
import co.edu.universe.repository.ClaseRepository;
import co.edu.universe.service.EstudianteService;
import co.edu.universe.service.HorarioService;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
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

    @FXML private Label lblMensaje;

    @FXML private GridPane gridCalendario;


    // ============================
    // INITIALIZE
    // ============================
    @FXML
    public void initialize() {

        // Configurar columnas
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesorNombre"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("horaFin"));

        // Cargar asignaturas
        comboAsignatura.getItems().addAll(asignaturaRepository.findAll());

        comboAsignatura.setOnAction(e -> cargarClases());

        construirCalendario();
    }
    // ============================
    // Settear estudiante desde login
    // ============================
    public void setEstudiante(Estudiante est) {
        this.estudiante = est;
        actualizarCalendario();
    }
    // ============================
    // CARGAR CLASES
    // ============================
    private void cargarClases() {
        Asignatura asignatura = comboAsignatura.getValue();
        if (asignatura == null) return;

        List<Clase> clases = claseRepository.findByAsignatura(asignatura);
        tablaClases.getItems().setAll(clases);
    }
    // ============================
    // AGREGAR CLASE
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

            Estudiante est = this.estudiante;

            horarioService.agregarClase(
                    est.getHorario().getId(),
                    clase.getId()
            );
            lblMensaje.setStyle("-fx-text-fill:green;");
            lblMensaje.setText("Clase agregada exitosamente.");

            actualizarCalendario();

        } catch (Exception ex) {
            lblMensaje.setStyle("-fx-text-fill:red;");
            lblMensaje.setText(ex.getMessage());
        }
    }
    // ============================
    // CONSTRUIR GRID VACÍO
    // ============================
    private void construirCalendario() {

        gridCalendario.getChildren().clear();
        gridCalendario.getColumnConstraints().clear();
        gridCalendario.getRowConstraints().clear();

        // Columnas (LUN-SAB)
        String[] dias = {"LUN", "MAR", "MIE", "JUE", "VIE", "SAB"};

        // Primera fila: encabezados
        gridCalendario.add(new Label(""), 0, 0); // esquina vacía

        for (int col = 0; col < dias.length; col++) {
            Label lbl = new Label(dias[col]);
            lbl.setStyle("-fx-font-weight: bold;");
            gridCalendario.add(lbl, col + 1, 0);
        }

        // Filas de horas: 8 AM → 6 PM
        int row = 1;
        for (int hora = 8; hora <= 18; hora++) {

            Label lblHora = new Label(hora + ":00");
            gridCalendario.add(lblHora, 0, row);

            for (int col = 1; col <= 6; col++) {
                Pane celda = new Pane();
                celda.setStyle("-fx-background-color:white; -fx-border-color:#d0d0d0;");
                gridCalendario.add(celda, col, row);
            }

            row++;
        }
    }
    // ============================
    // ACTUALIZAR CALENDARIO
    // ============================
    private void actualizarCalendario() {

        construirCalendario();

        Estudiante est = this.estudiante;

        List<Clase> clases = estudiante.getHorario().getClases();

        for (Clase clase : clases) {

            int col = clase.getDia().ordinal() + 1;   // LUN=0 → columna 1
            int row = clase.getHoraInicio().getHour() - 7;

            Label lbl = new Label(
                    clase.getAsignatura().getNombre() +
                            " (" + clase.getHoraInicio() + ")"
            );

            lbl.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                    "-fx-padding: 4; -fx-font-size: 12;");

            StackPane contenedor = new StackPane(lbl);
            contenedor.setAlignment(Pos.CENTER);

            gridCalendario.add(contenedor, col, row);
        }
    }
}
