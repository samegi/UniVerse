package co.edu.universe.utils;

import co.edu.universe.UniverseApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContext {

    private static ConfigurableApplicationContext context;

    static {
        // Spring Boot sin servidor web cuando se ejecuta desde JavaFX
        // Para acceder a H2 Console, ejecutar spring-boot:run por separado
        context = new SpringApplicationBuilder(UniverseApplication.class)
            .headless(false)
            .web(org.springframework.boot.WebApplicationType.NONE)
            .run();
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}

