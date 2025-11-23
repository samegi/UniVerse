# UniVerse - Campus Virtual

## Cómo Ejecutar

### Opción 1: Aplicación JavaFX (Interfaz Gráfica)

```bash
.\mvnw.cmd exec:java
```

Esto abre la aplicación de escritorio.

### Opción 2: Spring Boot con H2 Console (Base de Datos)

```bash
.\mvnw.cmd spring-boot:run
```

Luego abre en tu navegador: `http://localhost:8080/h2-console`

**Datos de conexión:**
- JDBC URL: `jdbc:h2:file:./data/universidaddb`
- Usuario: `sa`
- Password: (dejar vacío)

### Ejecutar Ambos

Puedes ejecutar ambos comandos en terminales diferentes para tener la aplicación y la consola de base de datos al mismo tiempo.

