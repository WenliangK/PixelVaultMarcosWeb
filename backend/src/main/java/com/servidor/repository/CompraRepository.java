package com.servidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.servidor.model.Compra;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuarioIdOrderByFechaCompraDesc(Long usuarioId);
}
