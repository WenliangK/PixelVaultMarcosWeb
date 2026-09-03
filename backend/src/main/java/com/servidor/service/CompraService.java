package com.servidor.service;

import com.servidor.dto.CompraRequest;
import com.servidor.model.Compra;
import com.servidor.model.Cupon;
import java.util.List;

public interface CompraService {
    Compra crearCompra(Long usuarioId, CompraRequest request);
    List<Compra> historialPorUsuario(Long usuarioId);
    Cupon validarCupon(String codigo);
}
