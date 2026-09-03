package com.servidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.servidor.model.Juego;

import java.util.List;

public interface JuegoRepository extends JpaRepository<Juego, Long> {
    List<Juego> findByPlataformaGrupo(String plataformaGrupo);
    List<Juego> findByNombreContainingIgnoreCase(String nombre);
}
