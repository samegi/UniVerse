package co.edu.universe.service;

import co.edu.universe.model.Rol;
import co.edu.universe.model.Usuario;
import co.edu.universe.repository.RolRepository;
import co.edu.universe.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    // Crear usuario con Rol
    public Usuario crearUsuarioConRol(String nombre, String nombreRol) {

        // Validación: no duplicar usuario
        if (usuarioRepository.existsByNombre(nombre)) {
            throw new RuntimeException("El usuario ya existe");
        }

        Rol rol = rolRepository.findByNombreRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setRol(rol);

        return usuarioRepository.save(user);
    }
    //Buscar usuario por ID
    public Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    //Buscar Usuario por Nombre
    public Usuario login(String nombre) {
        return usuarioRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}


