package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Asignacion;
import co.edu.universe.model.Clase;
import co.edu.universe.model.Profesor;
import co.edu.universe.service.AsignacionService;
import co.edu.universe.service.ClaseService;
import co.edu.universe.service.ProfesorService;
import co.edu.universe.service.SalonService;
import co.edu.universe.utils.Paths;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrudAsignacionController {

    private final AsignacionService asignacionService;
    private final ProfesorService profesorService;
    private final ClaseService claseService;

    public CrudAsignacionController(AsignacionService asignacionService,
                                    ProfesorService profesorService,
                                    ClaseService claseService
    ){
        this.asignacionService = asignacionService;
        this.profesorService = profesorService;
        this.claseService = claseService;
    }

    @FXML
    private ComboBox<Profesor> cmbProfesor;
    @FXML private ComboBox<Clase> cmbClase;

    @FXML private TableView<Asignacion> tblAsignaciones;
    @FXML private TableColumn<Asignacion, String> colProfesor;
    @FXML private TableColumn<Asignacion, String> colClase;
    @FXML private TableColumn<Asignacion, Integer> colHoras;
    @FXML private TableColumn<Asignacion, Boolean> colNocturna;

    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    private ObservableList<Asignacion> listaAsignaciones = FXCollections.observableArrayList();

    // ===========================================================
    // INITIALIZE
    // ===========================================================
    @FXML
    public void initialize() {
        cargarCombos();
        configurarColumnas();
        cargarDatos();
        configurarSeleccionTabla();
    }

    private void cargarCombos() {
        cmbProfesor.setItems(FXCollections.observableArrayList(
                profesorService.listarProfesores()
        ));

        cmbClase.setItems(FXCollections.observableArrayList(
                claseService.listarClases()
        ));

        // Mostrar nombre del profesor
        cmbProfesor.setCellFactory(param -> new ListCell<Profesor>() {
            @Override
            protected void updateItem(Profesor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cmbProfesor.setButtonCell(new ListCell<Profesor>() {
            @Override
            protected void updateItem(Profesor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        // Mostrar asignatura + número de clase
        cmbClase.setCellFactory(param -> new ListCell<Clase>() {
            @Override
            protected void updateItem(Clase item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getAsignatura().getNombre() + " #" + item.getId());
                }
            }
        });
        cmbClase.setButtonCell(new ListCell<Clase>() {
            @Override
            protected void updateItem(Clase item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getAsignatura().getNombre() + " #" + item.getId());
                }
            }
        });
    }

    private void configurarColumnas() {
        colProfesor.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getProfesor().getNombre()
                )
        );

        colClase.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getClase().getAsignatura().getNombre()
                                + " #" + c.getValue().getClase().getId()
                )
        );

        colHoras.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getHoras()).asObject()
        );

        colNocturna.setCellValueFactory(c ->
                new SimpleBooleanProperty(c.getValue().isNocturna()).asObject()
        );
    }

    private void cargarDatos() {
        listaAsignaciones.setAll(asignacionService.obtenerAsignaciones());
        tblAsignaciones.setItems(listaAsignaciones);
    }

    private void configurarSeleccionTabla() {
        tblAsignaciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, sel) -> {
                    if (sel != null) {
                        cmbProfesor.setValue(sel.getProfesor());
                        cmbClase.setValue(sel.getClase());
                    }
                });
    }

    // ===========================================================
    // CRUD
    // ===========================================================

    @FXML
    void guardarAsignacion(ActionEvent event) {
        try {
            Profesor p = cmbProfesor.getValue();
            Clase c = cmbClase.getValue();

            if (p == null || c == null) {
                mostrarError("Campos incompletos", "Seleccione profesor y clase.");
                return;
            }

            asignacionService.crearAsignacion(p, c);
            cargarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    void eliminarAsignacion(ActionEvent event) {
        Asignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Sin selección", "Seleccione una asignación para eliminar.");
            return;
        }

        try {
            asignacionService.eliminarAsignacion(seleccionada.getId());
            cargarDatos();
            limpiarCampos();
        } catch (Exception e) {
            mostrarError("Error al eliminar", e.getMessage());
        }
    }

    // ===========================================================
    // AUXILIARES
    // ===========================================================
    private void limpiarCampos() {
        cmbProfesor.setValue(null);
        cmbClase.setValue(null);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void regresarMenu(ActionEvent event) {
        App.setRoot(Paths.MENU_DIR_DEPARTAMENTO);
    }
}
