package co.edu.universe.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Service
public class ReporteService {

    public File exportarReporteCSV(List<String> reporte, String nombreArchivo) {
        try {
            // Ruta temporal
            File archivo = new File(System.getProperty("java.io.tmpdir"), nombreArchivo + ".csv");

            try (PrintWriter writer = new PrintWriter(new FileWriter(archivo, false))) {

                writer.println("Estudiante,Asignatura,Clase");

                for (String linea : reporte) {
                    // separar usando delimitadores del texto generado
                    // Formato esperado:
                    // "Estudiante: X retirado de Asignatura: Y | Clase #Z"
                    String[] partes = linea.split(" retirado de Asignatura: ");
                    String estudiante = partes[0].replace("Estudiante: ", "");

                    String[] asignaturaYClase = partes[1].split(" \\| Clase #");
                    String asignatura = asignaturaYClase[0];
                    String clase = asignaturaYClase[1];

                    // Escribir fila en CSV
                    writer.println(estudiante + "," + asignatura + "," + clase);
                }
            }

            return archivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al generar CSV: " + e.getMessage(), e);
        }
    }
}

