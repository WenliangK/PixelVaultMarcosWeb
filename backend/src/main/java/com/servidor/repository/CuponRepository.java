package com.servidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.servidor.model.Cupon;

import java.util.Optional;

public interface CuponRepository extends JpaRepository<Cupon, Long> {
    Optional<Cupon> findByCodigoIgnoreCase(String codigo);
}
