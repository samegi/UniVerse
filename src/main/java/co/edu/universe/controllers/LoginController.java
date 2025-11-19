package co.edu.universe.controllers;

import co.edu.universe.App;
import co.edu.universe.model.Usuario;
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
            // Buscar usuario
            Usuario usuario = usuarioService.login(nombre.trim());

            if (usuario == null) {
                lblError.setText("Usuario no encontrado.");
                return;
            }

            // Guardar usuario en sesión
            Sesion.setUsuario(usuario);

            // Obtener rol
            String rol = usuario.getRol().getNombre();

            // Navegación según rol
            if (rol.equalsIgnoreCase("DirectorCarrera")) {
                App.setRoot(Paths.MENU_DIR_CARRERA);
            } else {
                //ELIMINAR DESPUES
                lblError.setText("Tu rol no tiene una vista asignada.");
            }

        } catch (Exception e) {
            lblError.setText("Usuario no encontrado.");
        }
    }
}
