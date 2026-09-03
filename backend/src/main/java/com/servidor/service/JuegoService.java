package com.servidor.service;

import com.servidor.model.Juego;
import java.util.List;

public interface JuegoService {
    List<Juego> listarTodos();
    List<Juego> filtrarPorPlataforma(String plataformaGrupo);
    Juego buscarPorId(Long id);
}
