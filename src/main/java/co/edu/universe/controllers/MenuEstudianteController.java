package co.edu.universe.controllers;
import co.edu.universe.App;
import co.edu.universe.model.Estudiante;
import co.edu.universe.model.Usuario;
import co.edu.universe.service.EstudianteService;

import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
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

    private Long estudianteId;

    @FXML
    public void initialize() {

        Usuario usuario = Sesion.getUsuario();

        if (usuario != null && usuario.getEstudiante() != null) {
            Estudiante est = usuario.getEstudiante();
            lblBienvenida.setText("Bienvenido, " + est.getNombre());
        }
    }

    // ============================
    // ABRIR PANTALLA INSCRIBIR CLASES
    // ============================
    @FXML
    public void abrirInscribirClases() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InscribirHorario.fxml"));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            InscribirHorarioController controller = loader.getController();
            controller.setEstudiante(Sesion.getUsuario().getEstudiante());

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================
    // ABRIR PANTALLA HORARIO (solo visual)
    // ============================
    @FXML
    public void abrirHorario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/VerHorario.fxml"));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            VerHorarioController controller = loader.getController();
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
        Sesion.setUsuario(null);                 // Limpia usuario logueado
        App.setRoot(Paths.LOGIN);
    }
}

