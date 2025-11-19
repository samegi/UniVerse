package co.edu.universe.service;

import co.edu.universe.model.Salon;
import co.edu.universe.repository.SalonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalonService {

    private final SalonRepository repo;

    public SalonService(SalonRepository repo) {
        this.repo = repo;
    }

    public List<Salon> listarSalones() {
        return repo.findAll();
    }

    public Salon buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Salón no encontrado"));
    }

    /*@Transactional
    public Salon crearSalon(Salon salon) {

        if (repo.existsByNombre(salon.getNombre())) {
            throw new IllegalArgumentException("Ya existe un salón con ese nombre");
        }

        return repo.save(salon);
    }

    @Transactional
    public Salon actualizarSalon(Long id, Salon nuevo) {

        Salon existente = buscarPorId(id);

        if (!existente.getNombre().equals(nuevo.getNombre())
                && repo.existsByNombre(nuevo.getNombre())) {
            throw new IllegalArgumentException("Ya existe un salón con ese nombre");
        }

        existente.setNombre(nuevo.getNombre());
        existente.setCupoMaximo(nuevo.getCupoMaximo());

        return repo.save(existente);
    }

    @Transactional
    public void eliminarSalon(Long id) {
        Salon salon = buscarPorId(id);
        repo.delete(salon);
    }*/
}

