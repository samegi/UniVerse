package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Semestre;
import co.edu.universe.service.SemestreService;
import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class CrudSemestreController {

    @Autowired
    private SemestreService semestreService;

    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    @FXML private TableColumn<Semestre, String> colFechaFin;
    @FXML private TableColumn<Semestre, String> colFechaInicio;
    @FXML private TableColumn<Semestre, String> colNombreSemestre;

    @FXML private DatePicker dpFechaFin;
    @FXML private DatePicker dpFechaInicio;

    @FXML private TableView<Semestre> tblSemestre;

    @FXML private TextField txtAño;
    @FXML private TextField txtNombreSemestre;
    @FXML private TextField txtPeriodo;

    private ObservableList<Semestre> listaSemestres;

    // --------------------------------------------------------------
    // INICIALIZACIÓN
    // --------------------------------------------------------------

    @FXML
    public void initialize() {
        configurarTabla();
        cargarSemestres();
        configurarSeleccionTabla();
    }

    private void configurarTabla() {
        colNombreSemestre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre())
        );

        colFechaInicio.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaInicio().toString())
        );

        colFechaFin.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaFin().toString())
        );
    }

    private void cargarSemestres() {
        listaSemestres = FXCollections.observableArrayList(
                semestreService.listarSemestre()
        );
        tblSemestre.setItems(listaSemestres);
    }

    private void configurarSeleccionTabla() {
        tblSemestre.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) llenarCamposDesdeTabla(newSel);
        });
    }

    private void llenarCamposDesdeTabla(Semestre s) {
        txtNombreSemestre.setText(s.getNombre());
        txtAño.setText(String.valueOf(s.getAño()));
        txtPeriodo.setText(String.valueOf(s.getPeriodo()));

        dpFechaInicio.setValue(LocalDate.ofInstant(s.getFechaInicio(), ZoneId.systemDefault()));
        dpFechaFin.setValue(LocalDate.ofInstant(s.getFechaFin(), ZoneId.systemDefault()));
    }
    // --------------------------------------------------------------
    // VALIDACIONES
    // --------------------------------------------------------------
    private boolean validarUnicidad(Semestre s, boolean esActualizacion, Long idActual) {

        // → Validar nombre único
        if (semestreService.existeNombre(s.getNombre(), esActualizacion ? idActual : null)) {
            mostrar("Nombre ya existe.", Alert.AlertType.WARNING);
            return false;
        }

        // → Validar fecha de inicio única
        if (semestreService.existeFechaInicio(s.getFechaInicio(), esActualizacion ? idActual : null)) {
            mostrar("Ya existe un semestre con esa fecha de inicio.", Alert.AlertType.WARNING);
            return false;
        }

        // → Validar fecha de fin única
        if (semestreService.existeFechaFin(s.getFechaFin(), esActualizacion ? idActual : null)) {
            mostrar("Ya existe un semestre con esa fecha de fin.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }
    // --------------------------------------------------------------
    // CRUD
    // --------------------------------------------------------------

    @FXML
    void crearSemestre(ActionEvent event) {
        try {
            Semestre semestre = construirSemestreDesdeFormulario();

            if (!validarUnicidad(semestre, false, null))
                return;

            semestreService.crearSemestre(semestre, Sesion.getUsuario());
            mostrar("Semestre creado correctamente", Alert.AlertType.INFORMATION);

            cargarSemestres();
            limpiarCampos();

        } catch (Exception e) {
            mostrar("Error al crear semestre: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    void actualizarSemestre(ActionEvent event) {
        Semestre seleccionado = tblSemestre.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrar("Seleccione un semestre", Alert.AlertType.WARNING);
            return;
        }

        try {
            Semestre actualizado = construirSemestreDesdeFormulario();

            if (!validarUnicidad(actualizado, true, seleccionado.getId()))
                return;

            semestreService.actualizarSemestre(seleccionado.getId(), actualizado, Sesion.getUsuario());

            mostrar("Semestre actualizado correctamente", Alert.AlertType.INFORMATION);

            cargarSemestres();
            limpiarCampos();

        } catch (Exception e) {
            mostrar("Error al actualizar semestre: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    void eliminarSemestre(ActionEvent event) {
        Semestre seleccionado = tblSemestre.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrar("Seleccione un semestre", Alert.AlertType.WARNING);
            return;
        }

        try {
            semestreService.eliminarSemestre(seleccionado.getId(), Sesion.getUsuario());
            mostrar("Semestre eliminado", Alert.AlertType.INFORMATION);

            cargarSemestres();
            limpiarCampos();

        } catch (Exception e) {
            mostrar("Error al eliminar semestre: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    // --------------------------------------------------------------
    // NAVEGACIÓN
    // --------------------------------------------------------------
    @FXML
    void regresarMenuDirCarrera(ActionEvent event) {
        App.setRoot(Paths.MENU_DIR_CARRERA);
    }
    // --------------------------------------------------------------
    // AUXILIARES
    // --------------------------------------------------------------
    private Semestre construirSemestreDesdeFormulario() {
        Semestre s = new Semestre();

        s.setNombre(txtNombreSemestre.getText());
        s.setAño(Integer.parseInt(txtAño.getText()));
        s.setPeriodo(Integer.parseInt(txtPeriodo.getText()));

        LocalDate inicio = dpFechaInicio.getValue();
        LocalDate fin = dpFechaFin.getValue();

        s.setFechaInicio(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
        s.setFechaFin(fin.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return s;
    }

    private void limpiarCampos() {
        txtNombreSemestre.clear();
        txtAño.clear();
        txtPeriodo.clear();
        dpFechaInicio.setValue(null);
        dpFechaFin.setValue(null);
    }

    private void mostrar(String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle("Información");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
