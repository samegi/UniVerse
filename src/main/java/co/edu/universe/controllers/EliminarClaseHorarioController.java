package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.*;
import co.edu.universe.service.HorarioService;
import co.edu.universe.service.EstudianteService;
import co.edu.universe.utils.Paths;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EliminarClaseHorarioController {

    private final HorarioService horarioService;
    private final EstudianteService estudianteService;

    @FXML private TableView<Clase> tablaClases;

    @FXML private TableColumn<Clase, String> colAsignatura;
    @FXML private TableColumn<Clase, String> colProfesor;
    @FXML private TableColumn<Clase, DiaSemana> colDia;
    @FXML private TableColumn<Clase, LocalTime> colInicio;
    @FXML private TableColumn<Clase, LocalTime> colFin;
    @FXML private TableColumn<Clase, Salon> colSalon;
    @FXML private TableColumn<Clase, Integer> colCupo;

    @FXML private ProgressBar barraCreditos;
    @FXML private Label lblCreditos;
    @FXML private Label lblMensaje;

    private Estudiante estudiante;

    // -------------------------------------------------------------------
    // INITIALIZE — SOLO CONFIGURA COLUMNAS
    // -------------------------------------------------------------------
    @FXML
    public void initialize() {

        colAsignatura.setCellValueFactory(
                cell -> new SimpleObjectProperty<>(cell.getValue().getAsignatura().getNombre())
        );
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesorNombre"));
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("horaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("horaFin"));
        colSalon.setCellValueFactory(new PropertyValueFactory<>("salon"));
        colCupo.setCellValueFactory(
                cell -> new SimpleObjectProperty<>(cell.getValue().getSalon().getCupoMax())
        );
    }

    // -------------------------------------------------------------------
    // SE SETEA EL ESTUDIANTE DESDE EL MENÚ
    // -------------------------------------------------------------------
    public void setEstudiante(Estudiante est) {
        this.estudiante = est;
        cargarClases();
        actualizarBarraCreditos();
    }

    // -------------------------------------------------------------------
    // CARGAR CLASES DEL HORARIO REAL DEL ESTUDIANTE
    // -------------------------------------------------------------------
    private void cargarClases() {

        // Traer estudiante REAL desde BD (con horario y clases)
        Estudiante estActualizado = estudianteService.obtenerEstudiante(estudiante.getId());
        this.estudiante = estActualizado;

        List<Clase> clases = estActualizado.getHorario().getClases();

        tablaClases.getItems().setAll(clases);
    }

    // -------------------------------------------------------------------
    // ELIMINAR CLASE DEL HORARIO
    // -------------------------------------------------------------------
    @FXML
    public void eliminarClase() {

        Clase seleccionada = tablaClases.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Seleccione una clase para eliminar.");
            return;
        }

        try {

            horarioService.eliminarClase(
                    estudiante.getHorario().getId(),
                    seleccionada.getId()
            );

            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Clase eliminada correctamente.");

            cargarClases();
            actualizarBarraCreditos();

        } catch (Exception ex) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText(ex.getMessage());
        }
    }

    // -------------------------------------------------------------------
    // CRÉDITOS
    // -------------------------------------------------------------------
    private int calcularCreditos() {
        return estudiante.getHorario()
                .getClases()
                .stream()
                .mapToInt(c -> c.getAsignatura().getCreditos())
                .sum();
    }

    private void actualizarBarraCreditos() {
        int total = calcularCreditos();
        barraCreditos.setProgress(total / 20.0);
        lblCreditos.setText(total + "/20 créditos");
    }

    // -------------------------------------------------------------------
    // VOLVER MENÚ
    // -------------------------------------------------------------------
    @FXML
    public void volverMenu() {
        App.setRoot(Paths.MENU_ESTUDIANTE);
    }
}
