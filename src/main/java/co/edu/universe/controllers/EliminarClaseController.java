package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Clase;
import co.edu.universe.service.ClaseService;
import co.edu.universe.utils.Paths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EliminarClaseController {

    @Autowired
    private ClaseService claseService;

    @FXML
    private BarChart<String, Number> graficoClases;

    @FXML
    private ComboBox<Clase> comboClases;

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

        List<String> reporte = claseService.eliminarClaseYRetirarEstudiantes(seleccionada.getId());

        mostrarReporte(reporte);

        cargarGrafico();
        cargarClases();
    }
    public void volverCrudClase(ActionEvent event){
        App.setRoot(Paths.CRUD_CLASE);
    }

    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void mostrarReporte(List<String> reporte) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Estudiantes Retirados");
        alert.setHeaderText("Reporte generado");

        StringBuilder sb = new StringBuilder();
        reporte.forEach(r -> sb.append(r).append("\n"));

        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
}

