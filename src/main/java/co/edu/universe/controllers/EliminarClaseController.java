package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Clase;
import co.edu.universe.service.ClaseService;
import co.edu.universe.service.ReporteService;
import co.edu.universe.utils.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Component
public class EliminarClaseController {

    private final ReporteService reporteService;
    private final ClaseService claseService;
    private List<String> ultimoReporte;
    private File ultimoArchivoCsv;

    @FXML
    private BarChart<String, Number> graficoClases;

    @FXML
    private ComboBox<Clase> comboClases;

    public EliminarClaseController(ReporteService reporteService, ClaseService claseService) {
        this.reporteService = reporteService;
        this.claseService = claseService;
    }

    @FXML
    public void initialize() {
        cargarGrafico();
        cargarClases();
    }

    private void cargarGrafico() {
        graficoClases.getData().clear();

        Map<Clase, Integer> datos = claseService.obtenerCantidadEstudiantesPorClase();

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Estudiantes inscritos");

        datos.forEach((clase, cantidad) -> {
            String label = clase.getAsignatura().getNombre() + " #" + clase.getId();
            serie.getData().add(new XYChart.Data<>(label, cantidad));
        });

        graficoClases.getData().add(serie);
    }

    private void cargarClases() {
        comboClases.getItems().setAll(claseService.listarClases());
    }

    @FXML
    private void onEliminarClick() {
        Clase seleccionada = comboClases.getValue();

        if (seleccionada == null) {
            mostrar("Seleccione una clase para eliminar.");
            return;
        }

        // 1️⃣ Ejecutar eliminación y obtener reporte
        List<String> reporte = claseService.eliminarClaseYRetirarEstudiantes(seleccionada.getId());

        // 2️⃣ Actualizar la UI
        cargarGrafico();
        cargarClases();

        // 3️⃣ Generar CSV
        File csv = reporteService.exportarReporteCSV(reporte, "reporte_clase_" + seleccionada.getId());

        // 4️⃣ GUARDAR LOS DATOS PARA EL BOTÓN "EXPORTAR CSV"
        ultimoReporte = reporte;
        ultimoArchivoCsv = csv;

        // 5️⃣ Mostrar ruta del archivo
        mostrar("Reporte CSV generado en:\n" + csv.getAbsolutePath());
    }

    @FXML
    private void onExportarCsvClick() {
        if (ultimoReporte == null) {
            mostrar("Primero elimine una clase para generar un reporte.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File destino = chooser.showSaveDialog(null);

        if (destino != null) {
            try {
                Files.copy(ultimoArchivoCsv.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                mostrar("CSV guardado exitosamente en:\n" + destino.getAbsolutePath());
            } catch (IOException e) {
                mostrar("Error al guardar CSV: " + e.getMessage());
            }
        }
    }
    public void volverCrudClase(ActionEvent event){
        App.setRoot(Paths.CRUD_CLASE);
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

}

