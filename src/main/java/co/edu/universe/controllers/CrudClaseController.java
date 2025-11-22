package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.*;
import co.edu.universe.service.*;
import co.edu.universe.utils.Paths;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class CrudClaseController {

    // ====== Inyección de servicios ======
    private final ClaseService claseService;
    private final ProfesorService profesorService;
    private final SalonService salonService;
    private final SemestreService semestreService;
    private final AsignaturaService asignaturaService;

    public CrudClaseController(
            ClaseService claseService,
            ProfesorService profesorService,
            SalonService salonService,
            SemestreService semestreService,
            AsignaturaService asignaturaService
    ) {
        this.claseService = claseService;
        this.profesorService = profesorService;
        this.salonService = salonService;
        this.semestreService = semestreService;
        this.asignaturaService = asignaturaService;
    }

    // ====== Componentes JavaFX ======
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardar;
    @FXML private Button btnVolver;

    @FXML private ComboBox<Asignatura> cmbAsignatura;
    @FXML private ComboBox<DiaSemana> cmbDia;
    @FXML private ComboBox<Profesor> cmbProfesor1;
    @FXML private ComboBox<Profesor> cmbProfesor2;
    @FXML private ComboBox<Salon> cmbSalon;
    @FXML private ComboBox<Semestre> cmbSemestre;

    @FXML private TableColumn<Clase, String> colAsignatura;
    @FXML private TableColumn<Clase, String> colDia;
    @FXML private TableColumn<Clase, String> colHoraFin;
    @FXML private TableColumn<Clase, String> colHoraInicio;

    @FXML private TableView<Clase> tblClases;
    @FXML private Spinner<Integer> spnHoraInicio;
    @FXML private Spinner<Integer> spnMinutoInicio;
    @FXML private Spinner<Integer> spnHoraFin;
    @FXML private Spinner<Integer> spnMinutoFin;

    // ====== Inicialización ======
    @FXML
    public void initialize() {
        cargarCombos();
        configurarTabla();
        cargarClases();
        configurarSpinnersDeHora();
    }

    // ====== Configurar Spinners ======
    private void configurarSpinnersDeHora() {

        // HORAS 0–23
        SpinnerValueFactory<Integer> horasFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);

        // MINUTOS 0–59 en pasos de 5
        SpinnerValueFactory<Integer> minutosFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5);

        spnHoraInicio.setValueFactory(horasFactory);
        spnMinutoInicio.setValueFactory(minutosFactory);

        spnHoraFin.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10)
        );
        spnMinutoFin.setValueFactory(minutosFactory);
    }

    // ====== Cargar valores ComboBoxes ======
    private void cargarCombos() {
        cmbAsignatura.setItems(FXCollections.observableArrayList(asignaturaService.listarAsignaturas()));
        cmbProfesor1.setItems(FXCollections.observableArrayList(profesorService.listarProfesores()));
        cmbProfesor2.setItems(FXCollections.observableArrayList(profesorService.listarProfesores()));
        cmbSalon.setItems(FXCollections.observableArrayList(salonService.listarSalones()));
        cmbSemestre.setItems(FXCollections.observableArrayList(semestreService.listarSemestre()));

        cmbDia.setItems(FXCollections.observableArrayList(DiaSemana.values()));
    }

    // ====== Configurar tabla ======
    private void configurarTabla() {
        colAsignatura.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getAsignatura().getNombre()
                )
        );

        colDia.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDia().name()   // LUNES, MARTES, ...
                )
        );

        colHoraInicio.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraInicio().toString()
                )
        );

        colHoraFin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraFin().toString()
                )
        );
    }

    // ====== Cargar tabla ======
    private void cargarClases() {
        tblClases.setItems(FXCollections.observableArrayList(claseService.listarClases()));
    }

    // ====== Guardar clase ======
    @FXML
    void guardarClase(ActionEvent event) {
        try {
            Clase clase = construirClaseDesdeFormulario();
            claseService.crearClase(clase);
            mostrarAlerta("Clase guardada exitosamente.");
            cargarClases();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    // ====== Actualizar clase ======
    @FXML
    void actualizarClase(ActionEvent event) {
        Clase seleccionada = tblClases.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Debes seleccionar una clase.");
            return;
        }

        try {
            Clase claseEditada = construirClaseDesdeFormulario();

            claseService.updateClase(
                    seleccionada.getId(),
                    claseEditada
            );

            mostrarAlerta("Clase actualizada correctamente.");
            cargarClases();

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    // ====== Eliminar clase ======
    @FXML
    void pantallaEliminarClase(ActionEvent event) {
        App.setRoot(Paths.ELIMINAR_CLASE);
    }

    // ====== Volver ======
    @FXML
    void regresarMenu(ActionEvent event) {
        App.setRoot(Paths.MENU_DIR_CARRERA);
    }

    // ====== Construir objeto Clase ======
    private Clase construirClaseDesdeFormulario() {
        Clase c = new Clase();

        c.setAsignatura(cmbAsignatura.getValue());
        c.setDia(cmbDia.getValue());

        // LocalTime desde Spinner
        LocalTime horaInicio = LocalTime.of(
                spnHoraInicio.getValue(),
                spnMinutoInicio.getValue()
        );

        LocalTime horaFin = LocalTime.of(
                spnHoraFin.getValue(),
                spnMinutoFin.getValue()
        );

        c.setHoraInicio(horaInicio);
        c.setHoraFin(horaFin);
        c.setSalon(cmbSalon.getValue());
        c.setSemestre(cmbSemestre.getValue());
        Salon s = cmbSalon.getValue();
        System.out.println("============== DEBUG SALON ==============");
        if (s == null) {
            System.out.println("El salón seleccionado es NULL");
        } else {
            System.out.println("Salon seleccionado: " + s.getNombre());
            System.out.println("ID: " + s.getId());
            System.out.println("CupoMax: " + s.getCupoMax());
        }
        System.out.println("==========================================");

        return c;
    }

    // ====== Utilidades ======
    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).show();
    }

    private void mostrarAlerta(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
}
