package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.*;
import co.edu.universe.service.*;
import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML private Button btnEliminarGrupo;
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
        configurarSeleccionTabla();
    }

    // ====== Configurar selección de tabla ======
    private void configurarSeleccionTabla() {
        tblClases.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                // Recargar la clase desde el servicio para inicializar relaciones lazy
                try {
                    Clase claseCompleta = claseService.buscarClasePorId(newSel.getId());
                    llenarFormularioDesdeClase(claseCompleta);
                } catch (Exception e) {
                    System.err.println("=== ERROR AL CARGAR CLASE ===");
                    System.err.println("Mensaje: " + e.getMessage());
                    e.printStackTrace();
                    System.err.println("============================");
                }
            }
        });
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
            // Imprimir error completo en consola
            System.err.println("=== ERROR AL GUARDAR CLASE ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("==============================");
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
            // Imprimir error completo en consola
            System.err.println("=== ERROR AL ACTUALIZAR CLASE ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=================================");
            mostrarError(e.getMessage());
        }
    }

    // ====== Eliminar clase (solo la clase seleccionada) ======
    @FXML
    void eliminarClase(ActionEvent event) {
        Clase seleccionada = tblClases.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarError("Debes seleccionar una clase para eliminar.");
            return;
        }

        try {
            claseService.eliminarClase(seleccionada.getId());
            mostrarAlerta("Clase eliminada correctamente.");
            cargarClases();
            limpiarCampos();
        } catch (Exception e) {
            // Imprimir error completo en consola
            System.err.println("=== ERROR AL ELIMINAR CLASE ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===============================");
            mostrarError("Error al eliminar clase: " + e.getMessage());
        }
    }

    // ====== Eliminar grupo de clases (redirige a pantalla de eliminar) ======
    @FXML
    void eliminarGrupoClases(ActionEvent event) {
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

        // Configurar profesores desde los ComboBoxes
        List<Profesor> profesoresTemporales = new ArrayList<>();
        if (cmbProfesor1.getValue() != null) {
            profesoresTemporales.add(cmbProfesor1.getValue());
        }
        if (cmbProfesor2.getValue() != null) {
            profesoresTemporales.add(cmbProfesor2.getValue());
        }
        c.setProfesoresTemporales(profesoresTemporales);

        return c;
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
            mostrarError("No se seleccionó ningún archivo.");
            return;
        }

        try (InputStream input = new FileInputStream(file)) {

            List<Clase> creadas = claseService.cargarClasesDesdeJson(input, Sesion.getUsuario());

            // Si quieres refrescar la tabla
            cargarClases();
            
            mostrarAlerta("Se cargaron " + creadas.size() + " clase(s) correctamente.");

        } catch (Exception e) {
            // Imprimir error completo en consola
            System.err.println("=== ERROR AL CARGAR JSON ===");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("============================");
            
            // Mostrar mensaje de error al usuario
            String mensajeError = "Error al cargar JSON: " + e.getMessage();
            if (e.getCause() != null) {
                mensajeError += "\nCausa: " + e.getCause().getMessage();
            }
            mostrarError(mensajeError);
        }
    }


    // ====== Utilidades ======
    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        cmbAsignatura.setValue(null);
        cmbDia.setValue(null);
        cmbSalon.setValue(null);
        cmbSemestre.setValue(null);
        cmbProfesor1.setValue(null);
        cmbProfesor2.setValue(null);
        spnHoraInicio.getValueFactory().setValue(8);
        spnMinutoInicio.getValueFactory().setValue(0);
        spnHoraFin.getValueFactory().setValue(10);
        spnMinutoFin.getValueFactory().setValue(0);
        tblClases.getSelectionModel().clearSelection();
    }

    // ====== Llenar formulario desde clase seleccionada ======
    private void llenarFormularioDesdeClase(Clase clase) {
        // Asignatura
        cmbAsignatura.setValue(clase.getAsignatura());
        
        // Día
        cmbDia.setValue(clase.getDia());
        
        // Hora inicio
        spnHoraInicio.getValueFactory().setValue(clase.getHoraInicio().getHour());
        spnMinutoInicio.getValueFactory().setValue(clase.getHoraInicio().getMinute());
        
        // Hora fin
        spnHoraFin.getValueFactory().setValue(clase.getHoraFin().getHour());
        spnMinutoFin.getValueFactory().setValue(clase.getHoraFin().getMinute());
        
        // Salón
        cmbSalon.setValue(clase.getSalon());
        
        // Semestre
        cmbSemestre.setValue(clase.getSemestre());
        
        // Profesores (desde asignaciones)
        System.out.println("=== DEBUG: Cargando profesores ===");
        System.out.println("Clase ID: " + clase.getId());
        System.out.println("Asignaciones: " + (clase.getAsignaciones() != null ? clase.getAsignaciones().size() : "null"));
        
        if (clase.getAsignaciones() != null && !clase.getAsignaciones().isEmpty()) {
            List<Profesor> profesores = clase.getAsignaciones().stream()
                    .filter(a -> a.getProfesor() != null)
                    .map(Asignacion::getProfesor)
                    .distinct()
                    .collect(Collectors.toList());
            
            System.out.println("Profesores encontrados: " + profesores.size());
            
            if (!profesores.isEmpty()) {
                cmbProfesor1.setValue(profesores.get(0));
                System.out.println("Profesor 1: " + profesores.get(0).getNombre());
                if (profesores.size() > 1) {
                    cmbProfesor2.setValue(profesores.get(1));
                    System.out.println("Profesor 2: " + profesores.get(1).getNombre());
                } else {
                    cmbProfesor2.setValue(null);
                }
            } else {
                cmbProfesor1.setValue(null);
                cmbProfesor2.setValue(null);
                System.out.println("No se encontraron profesores válidos");
            }
        } else {
            cmbProfesor1.setValue(null);
            cmbProfesor2.setValue(null);
            System.out.println("No hay asignaciones para esta clase");
        }
        System.out.println("================================");
    }
}
