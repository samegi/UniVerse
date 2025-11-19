package co.edu.universe.utils;

import co.edu.universe.UniverseApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContext {

    private static ConfigurableApplicationContext context;

    static {
        context = new SpringApplicationBuilder(UniverseApplication.class).run();
    }

    public static ConfigurableApplicationContext getContext() {
        return context;
    }
}

