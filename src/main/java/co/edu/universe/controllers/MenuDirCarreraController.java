package co.edu.universe.controllers;
import co.edu.universe.App;
import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

@Component
public class MenuDirCarreraController {

    @FXML
    private Button btnAsignatura;

    @FXML
    private Button btnClase;

    @FXML
    private Button btnSalir;

    @FXML
    private Button btnSemestre;

    // -------------------------------------------------------------
    //                     CERRAR SESIÓN
    // -------------------------------------------------------------

    @FXML
    void cerrarSesion(ActionEvent event) {
        Sesion.setUsuario(null);                 // Limpia usuario logueado
        App.setRoot(Paths.LOGIN);                // Regresa al login
    }

    // -------------------------------------------------------------
    //                     GESTIONAR SEMESTRE
    // -------------------------------------------------------------

    @FXML
    void gestionarSemestre(ActionEvent event) {
        App.setRoot(Paths.CRUD_SEMESTRE);        // Va a CrudSemestre.fxml
    }

    // -------------------------------------------------------------
    //                    GESTIONAR ASIGNATURA
    // -------------------------------------------------------------

    @FXML
    void gestionarAsignatura(ActionEvent event) {
        App.setRoot(Paths.CRUD_ASIGNATURA);      // Va a CrudAsignatura.fxml
    }

    // -------------------------------------------------------------
    //                    GESTIONAR CLASE (Vacío)
    // -------------------------------------------------------------

    @FXML
    void gestionarClase(ActionEvent event) {
        /*TODO: implementar cuando tengas CrudClase.fxml*/
    }
}
