package co.edu.universe;

import co.edu.universe.utils.SpringContext;
import co.edu.universe.utils.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URL;

public class App extends Application {

    private static ConfigurableApplicationContext springContext;
    private static Stage primaryStage;

    @Override
    public void init() {
        springContext = SpringContext.getContext();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        setRoot(Paths.LOGIN);
        primaryStage.setTitle("Sistema Universidad");
        primaryStage.show();
    }

    public static void setRoot(String fxmlPath) {
        try {
            URL resource = App.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalStateException("No se encontró el archivo FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(springContext::getBean);

            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        springContext.close();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
