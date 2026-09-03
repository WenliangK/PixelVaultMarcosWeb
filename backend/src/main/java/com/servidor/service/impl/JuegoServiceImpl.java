package com.servidor.service.impl;

import org.springframework.stereotype.Service;

import com.servidor.model.Juego;
import com.servidor.repository.JuegoRepository;
import com.servidor.service.JuegoService;
import com.servidor.service.NegocioException;

import java.util.List;

@Service
public class JuegoServiceImpl implements JuegoService {

    private final JuegoRepository juegoRepository;

    public JuegoServiceImpl(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    @Override
    public List<Juego> listarTodos() {
        return juegoRepository.findAll();
    }

    @Override
    public List<Juego> filtrarPorPlataforma(String plataformaGrupo) {
        if (plataformaGrupo == null || plataformaGrupo.isBlank() || plataformaGrupo.equalsIgnoreCase("all")) {
            return listarTodos();
        }
        return juegoRepository.findByPlataformaGrupo(plataformaGrupo);
    }

    @Override
    public Juego buscarPorId(Long id) {
        return juegoRepository.findById(id)
                .orElseThrow(() -> new NegocioException("Juego no encontrado"));
    }
}
