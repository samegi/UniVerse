package co.edu.universe.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long idUsuario;
    private String nombre;
    private String rol;
}