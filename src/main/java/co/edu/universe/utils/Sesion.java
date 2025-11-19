package co.edu.universe.utils;

import co.edu.universe.model.Usuario;

public class Sesion {

    private static Usuario usuario;

    public static void setUsuario(Usuario u) {
        usuario = u;
    }

    public static Usuario getUsuario() {
        return usuario;
    }
}

