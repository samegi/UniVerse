package co.edu.universe.service;

import co.edu.universe.model.Usuario;
import co.edu.universe.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario login(String nombre) {
        return usuarioRepository.findByNombre(nombre)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));
    }
}

