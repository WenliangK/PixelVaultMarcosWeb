package com.servidor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.servidor.dto.CompraRequest;
import com.servidor.model.*;
import com.servidor.repository.CompraRepository;
import com.servidor.repository.CuponRepository;
import com.servidor.repository.JuegoRepository;
import com.servidor.repository.UsuarioRepository;
import com.servidor.service.CompraService;
import com.servidor.service.NegocioException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final JuegoRepository juegoRepository;
    private final CuponRepository cuponRepository;
    private final UsuarioRepository usuarioRepository;

    public CompraServiceImpl(CompraRepository compraRepository, JuegoRepository juegoRepository,
                              CuponRepository cuponRepository, UsuarioRepository usuarioRepository) {
        this.compraRepository = compraRepository;
        this.juegoRepository = juegoRepository;
        this.cuponRepository = cuponRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Compra crearCompra(Long usuarioId, CompraRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NegocioException("Usuario no encontrado"));

        Compra compra = new Compra();
        compra.setUsuario(usuario);

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CompraRequest.ItemCompraRequest item : request.getItems()) {
            Juego juego = juegoRepository.findById(item.getJuegoId())
                    .orElseThrow(() -> new NegocioException("El juego " + item.getJuegoId() + " no existe"));

            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new NegocioException("Cantidad invalida para " + juego.getNombre());
            }

            BigDecimal precioLinea = juego.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            subtotal = subtotal.add(precioLinea);

            DetalleCompra detalle = new DetalleCompra(compra, juego, item.getCantidad(), juego.getPrecio());
            compra.getDetalles().add(detalle);
        }

        BigDecimal descuento = BigDecimal.ZERO;
        if (request.getCuponCodigo() != null && !request.getCuponCodigo().isBlank()) {
            Cupon cupon = validarCupon(request.getCuponCodigo());
            compra.setCupon(cupon);
            descuento = subtotal.multiply(cupon.getPorcentajeDescuento())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        compra.setSubtotal(subtotal);
        compra.setDescuentoAplicado(descuento);
        compra.setTotal(subtotal.subtract(descuento));
        compra.setEstado("COMPLETADA");

        return compraRepository.save(compra);
    }

    @Override
    public List<Compra> historialPorUsuario(Long usuarioId) {
        return compraRepository.findByUsuarioIdOrderByFechaCompraDesc(usuarioId);
    }

    @Override
    public Cupon validarCupon(String codigo) {
        Cupon cupon = cuponRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new NegocioException("El codigo ingresado no es valido"));

        if (!Boolean.TRUE.equals(cupon.getActivo())) {
            throw new NegocioException("El cupon ya no esta activo");
        }
        if (cupon.getFechaExpiracion() != null && cupon.getFechaExpiracion().isBefore(LocalDate.now())) {
            throw new NegocioException("El cupon ha expirado");
        }
        return cupon;
    }
}
