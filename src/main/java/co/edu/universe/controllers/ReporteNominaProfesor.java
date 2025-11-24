package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Profesor;
import co.edu.universe.model.TipoProfesor;
import co.edu.universe.service.ProfesorService;
import co.edu.universe.utils.Paths;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;

@Component
public class ReporteNominaProfesor {

    @Autowired
    private ProfesorService profesorService;

    @FXML private ComboBox<Profesor> cmbProfesor;
    @FXML private Label lblTipo;
    @FXML private Label lblHoras;
    @FXML private Label lblHorasNoc;
    @FXML private Label lblSalario;
    @FXML private Label lblExtra;

    @FXML
    public void initialize() {
        cmbProfesor.setItems(FXCollections.observableArrayList(profesorService.listarProfesores()));
        cmbProfesor.setOnAction(e -> mostrarDetalle());
    }

    private void mostrarDetalle() {
        Profesor p = cmbProfesor.getValue();
        if (p == null) return;

        int horas = profesorService.calcularTotalHorasDictadas(p);
        int horasNoc = profesorService.calcularTotalHorasNocturnas(p);
        double salario = profesorService.calcularSalarioTotal(p);

        lblTipo.setText("Tipo: " + p.getTipoProfesor().name());
        lblHoras.setText("Horas dictadas: " + horas);
        lblHorasNoc.setText("Horas nocturnas: " + horasNoc);
        lblSalario.setText("Salario total: $" + salario);

        if (p.getTipoProfesor() == TipoProfesor.PLANTA && horas > p.getMaxHoras()) {
            lblExtra.setText("Horas extra: " + (horas - p.getMaxHoras()));
        } else {
            lblExtra.setText("Horas extra: 0");
        }
    }

    @FXML
    private void exportarTxt() {
        Profesor p = cmbProfesor.getValue();
        if (p == null) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar archivo de profesor");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
        File file = chooser.showSaveDialog(null);

        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(file)) {

            int horas = profesorService.calcularTotalHorasDictadas(p);
            int horasNoc = profesorService.calcularTotalHorasNocturnas(p);
            double salario = profesorService.calcularSalarioTotal(p);

            pw.println("Profesor: " + p.getNombre());
            pw.println("Tipo: " + p.getTipoProfesor());
            pw.println("Horas dictadas: " + horas);
            pw.println("Horas nocturnas: " + horasNoc);

            if (p.getTipoProfesor() == TipoProfesor.PLANTA && horas > p.getMaxHoras()) {
                pw.println("Horas extra: " + (horas - p.getMaxHoras()));
            }

            pw.println("Salario total: $" + salario);

        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Error exportando archivo: " + e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void volver() {
        App.setRoot(Paths.MENU_DIR_DEPARTAMENTO);
    }
}
