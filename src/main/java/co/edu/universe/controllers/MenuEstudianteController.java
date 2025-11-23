package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Estudiante;
import co.edu.universe.model.Usuario;
import co.edu.universe.service.EstudianteService;

import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuEstudianteController {

    private final EstudianteService estudianteService;
    private final ApplicationContext context;

    @FXML private Label lblBienvenida;

    private Estudiante estudiante;

    @FXML
    public void initialize() {

        Usuario usuario = Sesion.getUsuario();

        if (usuario != null && usuario.getEstudiante() != null) {
            this.estudiante = usuario.getEstudiante();
            lblBienvenida.setText("Bienvenido, " + estudiante.getNombre());
        }
    }

    // ============================
    // ABRIR PANTALLA INSCRIBIR HORARIO
    // ============================
    @FXML
    public void abrirInscribirClases() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.INSCRIBIR));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            // controlador correcto
            InscribirHorarioController controller = loader.getController();
            controller.setEstudiante(estudiante);

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================
    // ABRIR HORARIO (GRÁFICO)
    // ============================
    @FXML
    public void abrirHorario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.VER_HORARIO));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            // controlador correcto
            VerHorarioController controller = loader.getController();
            controller.setEstudiante(estudiante);

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================
    // ABRIR ELIMINAR CLASE DEL HORARIO
    // ============================
    @FXML
    void irAEliminarClase(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(Paths.ELIMINAR_CLASE_HORARIO));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            EliminarClaseHorarioController controller = loader.getController();
            controller.setEstudiante(Sesion.getUsuario().getEstudiante());

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================
    // CERRAR SESIÓN
    // ============================
    @FXML
    public void cerrarSesion() {
        Sesion.setUsuario(null);
        App.setRoot(Paths.LOGIN);
    }
}
