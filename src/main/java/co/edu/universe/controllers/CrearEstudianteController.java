package co.edu.universe.controllers;

import co.edu.universe.model.Nivel;
import co.edu.universe.model.Carrera;
import co.edu.universe.service.EstudianteService;
import co.edu.universe.repository.CarreraRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CrearEstudianteController {

    private final EstudianteService estudianteService;
    private final CarreraRepository carreraRepository;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<Carrera> comboCarrera;
    @FXML private TextField txtUsuario;
    @FXML private ComboBox<Nivel> comboNivelIngles;
    @FXML private TextField txtExamen;
    @FXML private Label lblMensaje;

    @FXML
    public void initialize() {
        // Cargar carreras en el combo
        List<Carrera> carreras = carreraRepository.findAll();
        comboCarrera.getItems().addAll(carreras);

        // Cargar niveles de inglés
        comboNivelIngles.getItems().addAll(Nivel.values());
    }

    @FXML
    public void registrarEstudiante() {
        try {

            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            Carrera carrera = comboCarrera.getValue();
            String user = txtUsuario.getText();
            Nivel nivel = comboNivelIngles.getValue();
            String examen = txtExamen.getText();

            if (nombre.isEmpty() || correo.isEmpty() || carrera == null ||
                    user.isEmpty() || nivel == null) {

                lblMensaje.setText("Complete todos los campos requeridos.");
                return;
            }

            estudianteService.crearEstudiante(
                    nombre,
                    correo,
                    carrera.getId(),
                    user,
                    nivel,
                    examen
            );

            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Estudiante registrado exitosamente.");

            limpiarCampos();

        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtCorreo.clear();
        comboCarrera.setValue(null);
        txtUsuario.clear();
        comboNivelIngles.setValue(null);
        txtExamen.clear();
    }
}

