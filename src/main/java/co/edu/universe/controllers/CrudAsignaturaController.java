package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Asignatura;
import co.edu.universe.model.Semestre;
import co.edu.universe.service.AsignaturaService;
import co.edu.universe.service.SemestreService;
import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Component
public class CrudAsignaturaController {

    @Autowired private AsignaturaService asignaturaService;
    @Autowired private SemestreService semestreService;

    @FXML private Button btnEliminar;
    @FXML private Button btnVolver;

    @FXML private ComboBox<String> cmbIngles;
    @FXML private ComboBox<Semestre> cmbSemestre;

    @FXML private TableColumn<Asignatura, Integer> colCreditos;
    @FXML private TableColumn<Asignatura, Boolean> colIngles;
    @FXML private TableColumn<Asignatura, String> colNombre;

    @FXML private TableView<Asignatura> tblAsignaturas;

    @FXML private TextField txtCreditos;
    @FXML private TextField txtNombre;

    private ObservableList<Asignatura> listaAsignaturas = FXCollections.observableArrayList();

    // ===========================================================
    //                       INITIALIZE
    // ===========================================================
    @FXML
    public void initialize() {

        // Inglés
        cmbIngles.setItems(FXCollections.observableArrayList("Sí", "No"));

        // Semestres
        cmbSemestre.setItems(FXCollections.observableArrayList(
                semestreService.listarSemestre()
        ));

        // Cómo se muestran los semestres
        cmbSemestre.setCellFactory(param -> new ListCell<Semestre>() {
            @Override
            protected void updateItem(Semestre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        cmbSemestre.setButtonCell(new ListCell<Semestre>() {
            @Override
            protected void updateItem(Semestre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });

        configurarColumnas();
        cargarDatos();
        configurarSeleccionTabla();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre())
        );
        colCreditos.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCreditos()).asObject()
        );
        colIngles.setCellValueFactory(c ->
                new javafx.beans.property.SimpleBooleanProperty(c.getValue().isIngles()).asObject()
        );
    }

    private void cargarDatos() {
        listaAsignaturas.setAll(asignaturaService.listarAsignaturas());
        tblAsignaturas.setItems(listaAsignaturas);
    }

    private void configurarSeleccionTabla() {
        tblAsignaturas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                txtNombre.setText(sel.getNombre());
                txtCreditos.setText(String.valueOf(sel.getCreditos()));
                cmbIngles.setValue(sel.isIngles() ? "Sí" : "No");
                cmbSemestre.setValue(sel.getSemestre());
            }
        });
    }


    // ===========================================================
    //                   CRUD OPERATIONS
    // ===========================================================

    @FXML
    void guardarAsignatura(ActionEvent event) {
        try {
            Asignatura nueva = construirAsignaturaDesdeFormulario();

            asignaturaService.crearAsignatura(nueva, Sesion.getUsuario());
            cargarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarError("Error al guardar", e.getMessage());
        }
    }

    @FXML
    void actualizarAsignatura(ActionEvent event) {
        Asignatura seleccionada = tblAsignaturas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selección vacía", "Seleccione una asignatura.");
            return;
        }

        try {
            Asignatura actualizada = construirAsignaturaDesdeFormulario();

            asignaturaService.actualizarAsignatura(seleccionada.getId(), actualizada, Sesion.getUsuario());
            cargarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarError("Error al actualizar", e.getMessage());
        }
    }

    @FXML
    void eliminarAsignatura(ActionEvent event) {
        Asignatura seleccionada = tblAsignaturas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Selección vacía", "Seleccione una asignatura.");
            return;
        }

        try {
            asignaturaService.eliminarAsignatura(seleccionada.getId(), Sesion.getUsuario());
            cargarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarError("Error al eliminar", e.getMessage());
        }
    }

    // ===========================================================
    //                       AUXILIARES
    // ===========================================================
    private Asignatura construirAsignaturaDesdeFormulario() {

        if (cmbSemestre.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar un semestre.");
        }

        Asignatura a = new Asignatura();

        a.setNombre(txtNombre.getText());
        a.setCreditos(Integer.parseInt(txtCreditos.getText()));
        a.setIngles(cmbIngles.getValue().equals("Sí"));
        a.setSemestre(cmbSemestre.getValue());

        return a;
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtCreditos.clear();
        cmbIngles.setValue(null);
        cmbSemestre.setValue(null);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    @FXML
    private void onCargarJsonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos JSON", "*.json")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file == null) {
            mostrarError("Archivo vacio", "No se seleccionó ningún archivo.");
            return;
        }

        try (InputStream input = new FileInputStream(file)) {

            List<Asignatura> creadas =
                    asignaturaService.cargarAsignaturasDesdeJson(input, Sesion.getUsuario());


            // Si quieres refrescar la tabla
            cargarDatos();

        } catch (Exception e) {
            mostrarError("Error JSON", "Error al cargar JSON: " + e.getMessage());
        }
    }
    // ===========================================================
    //                       VOLVER
    // ===========================================================
    @FXML
    void regresarMenu(ActionEvent event) {
        App.setRoot(Paths.MENU_DIR_CARRERA);
    }
}
