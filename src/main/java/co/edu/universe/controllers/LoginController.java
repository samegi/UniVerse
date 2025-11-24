package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Estudiante;
import co.edu.universe.model.Usuario;
import co.edu.universe.service.EstudianteService;
import co.edu.universe.service.UsuarioService;
import co.edu.universe.utils.Paths;
import co.edu.universe.utils.Sesion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @FXML
    private TextField txtNombre;

    @FXML
    private Label lblError;

    @FXML
    public void onLoginClick() {

        lblError.setText(""); // Limpiar error anterior

        String nombre = txtNombre.getText();

        // VALIDACION: Campo vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            lblError.setText("Ingrese un nombre de usuario.");
            return;
        }
        try {
            Usuario usuario = usuarioService.login(nombre.trim());

            if (usuario == null) {
                lblError.setText("Usuario no encontrado.");
                return;
            }

            // Guardar usuario en sesión
            Sesion.setUsuario(usuario);

            String rol = usuario.getRol().getNombreRol();

            if (rol.equalsIgnoreCase("DIRECTOR_CARRERA")) {
                App.setRoot(Paths.MENU_DIR_CARRERA);
            }
            else if (rol.equalsIgnoreCase("ESTUDIANTE")) {
                App.setRoot(Paths.MENU_ESTUDIANTE);
            }
            else if (rol.equalsIgnoreCase("DIRECTOR_DEPARTAMENTO")) {
                App.setRoot(Paths.MENU_DIR_DEPARTAMENTO);
            }

        } catch (Exception e) {
            lblError.setText("Error: " + e.getMessage());
        }

    }
    @FXML
    public void onRegisterClick() {
        App.setRoot(Paths.CREAR_ESTUDIANTE);
    }
}
