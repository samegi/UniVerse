package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Clase;
import co.edu.universe.model.DiaSemana;
import co.edu.universe.model.Estudiante;
import co.edu.universe.service.EstudianteService;

import co.edu.universe.utils.Paths;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VerHorarioController {

    private final EstudianteService estudianteService;

    @FXML private GridPane gridCalendario;

    private Estudiante estudiante;


    // ==================================
    // RECIBIR ID DEL ESTUDIANTE
    // ==================================
    public void setEstudiante(Estudiante est) {
        this.estudiante = est;
        cargarHorario();
    }


    // ==================================
    // CARGAR HORARIO
    // ==================================
    private void cargarHorario() {

        construirCalendario();
        Estudiante est = this.estudiante;
        List<Clase> clases = est.getHorario().getClases();

        for (Clase clase : clases) {

            int col = clase.getDia().ordinal() + 1;  // LUN=0 → Col=1
            int row = clase.getHoraInicio().getHour() - 7;

            Label lbl = new Label(
                    clase.getAsignatura().getNombre() +
                            "\n" +
                            clase.getHoraInicio() + " - " + clase.getHoraFin()
            );

            lbl.setStyle(
                    "-fx-background-color: #2d8f6f; " +
                            "-fx-text-fill: white; " +
                            "-fx-padding: 6; " +
                            "-fx-font-size: 12; " +
                            "-fx-border-color: #1f6c54;"
            );

            StackPane contenedor = new StackPane(lbl);
            contenedor.setAlignment(Pos.CENTER);

            gridCalendario.add(contenedor, col, row);
        }
    }


    // ==================================
    // CONSTRUIR GRID VACÍO
    // ==================================
    private void construirCalendario() {

        gridCalendario.getChildren().clear();
        gridCalendario.getColumnConstraints().clear();
        gridCalendario.getRowConstraints().clear();

        // Encabezados de días
        String[] dias = {"LUN", "MAR", "MIE", "JUE", "VIE", "SAB"};

        gridCalendario.add(new Label(""), 0, 0);

        for (int col = 0; col < dias.length; col++) {
            Label lbl = new Label(dias[col]);
            lbl.setStyle("-fx-font-weight:bold;");
            gridCalendario.add(lbl, col + 1, 0);
        }

        // Horas (8am a 6pm)
        int row = 1;
        for (int hora = 8; hora <= 18; hora++) {

            Label lblHora = new Label(hora + ":00");
            lblHora.setStyle("-fx-font-weight:bold;");
            gridCalendario.add(lblHora, 0, row);

            for (int col = 1; col <= 6; col++) {
                Pane celda = new Pane();
                celda.setStyle("-fx-background-color:white; -fx-border-color:#d0d0d0;");
                gridCalendario.add(celda, col, row);
            }

            row++;
        }
    }

    // ==================================
    // VOLVER AL MENÚ
    // ==================================
    @FXML
    public void volverMenu() {
        App.setRoot(Paths.MENU_ESTUDIANTE);
    }
}

