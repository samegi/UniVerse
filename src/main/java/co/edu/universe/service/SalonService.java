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
}

