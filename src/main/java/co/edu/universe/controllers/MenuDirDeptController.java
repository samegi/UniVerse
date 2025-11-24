package co.edu.universe.controllers;
import co.edu.universe.App;
import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class MenuDirDeptController {
    @FXML
    void cerrarSesion(ActionEvent event) {
        Sesion.setUsuario(null);
        App.setRoot(Paths.LOGIN);
    }

    @FXML
    void crudAsignaciones(ActionEvent event) {
        App.setRoot(Paths.CRUD_ASIGNACION);
    }

    @FXML
    void mostrarReportes(ActionEvent event) {
        App.setRoot(Paths.REPORTE_PROFE);
    }
}

