package com.servidor.service;

import com.servidor.dto.CompraRequest;
import com.servidor.model.Compra;
import com.servidor.model.Cupon;
import com.servidor.model.Juego;
import com.servidor.model.Usuario;
import com.servidor.repository.CompraRepository;
import com.servidor.repository.CuponRepository;
import com.servidor.repository.JuegoRepository;
import com.servidor.repository.UsuarioRepository;
import com.servidor.service.impl.CompraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de las reglas de negocio de compras y cupones (sin Spring ni base de datos). */
@ExtendWith(MockitoExtension.class)
class CompraServiceImplTest {

    @Mock private CompraRepository compraRepository;
    @Mock private JuegoRepository juegoRepository;
    @Mock private CuponRepository cuponRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private CompraServiceImpl service;

    @BeforeEach
    void preparar() {
        service = new CompraServiceImpl(compraRepository, juegoRepository, cuponRepository, usuarioRepository);
    }

    private Juego juego(long id, String precio) {
        Juego j = new Juego();
        j.setId(id);
        j.setNombre("Juego " + id);
        j.setPrecio(new BigDecimal(precio));
        j.setStock(10);
        return j;
    }

    private CompraRequest pedido(long juegoId, int cantidad, String cupon) {
        CompraRequest.ItemCompraRequest item = new CompraRequest.ItemCompraRequest();
        item.setJuegoId(juegoId);
        item.setCantidad(cantidad);
        CompraRequest req = new CompraRequest();
        req.setItems(List.of(item));
        req.setCuponCodigo(cupon);
        return req;
    }

    // ---------------------------------------------------------------- cupones (RF-05)
    @Test
    void validarCupon_codigoValido_devuelveElCupon() {
        Cupon c = new Cupon("PIXEL10", new BigDecimal("10.00"));
        when(cuponRepository.findByCodigoIgnoreCase("PIXEL10")).thenReturn(Optional.of(c));
        assertSame(c, service.validarCupon(" PIXEL10 "));
    }

    @Test
    void validarCupon_codigoInexistente_lanzaNegocioException() {
        when(cuponRepository.findByCodigoIgnoreCase("NOEXISTE")).thenReturn(Optional.empty());
        assertThrows(NegocioException.class, () -> service.validarCupon("NOEXISTE"));
    }

    @Test
    void validarCupon_inactivo_lanzaNegocioException() {
        Cupon c = new Cupon("VIEJO", new BigDecimal("5.00"));
        c.setActivo(false);
        when(cuponRepository.findByCodigoIgnoreCase("VIEJO")).thenReturn(Optional.of(c));
        assertThrows(NegocioException.class, () -> service.validarCupon("VIEJO"));
    }

    @Test
    void validarCupon_expirado_lanzaNegocioException() {
        Cupon c = new Cupon("VENCIDO", new BigDecimal("5.00"));
        c.setFechaExpiracion(LocalDate.now().minusDays(1));
        when(cuponRepository.findByCodigoIgnoreCase("VENCIDO")).thenReturn(Optional.of(c));
        assertThrows(NegocioException.class, () -> service.validarCupon("VENCIDO"));
    }

    // ---------------------------------------------------------------- compras (RF-04, RF-07)
    @Test
    void crearCompra_conCupon_calculaSubtotalDescuentoYTotal() {
        Usuario u = new Usuario("ana@correo.com", "hash");
        lenient().when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(juegoRepository.findById(5L)).thenReturn(Optional.of(juego(5, "100.00")));
        when(cuponRepository.findByCodigoIgnoreCase("PIXEL10"))
                .thenReturn(Optional.of(new Cupon("PIXEL10", new BigDecimal("10.00"))));
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        Compra compra = service.crearCompra(1L, pedido(5, 2, "PIXEL10"));

        assertEquals(0, new BigDecimal("200.00").compareTo(compra.getSubtotal()));
        assertEquals(0, new BigDecimal("20.00").compareTo(compra.getDescuentoAplicado()));
        assertEquals(0, new BigDecimal("180.00").compareTo(compra.getTotal()));
        assertEquals(1, compra.getDetalles().size());
        assertEquals("COMPLETADA", compra.getEstado());
    }

    @Test
    void crearCompra_sinCupon_noAplicaDescuento() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario("ana@correo.com", "hash")));
        when(juegoRepository.findById(5L)).thenReturn(Optional.of(juego(5, "58.00")));
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        Compra compra = service.crearCompra(1L, pedido(5, 1, null));

        assertEquals(0, BigDecimal.ZERO.compareTo(compra.getDescuentoAplicado()));
        assertEquals(0, new BigDecimal("58.00").compareTo(compra.getTotal()));
    }

    @Test
    void crearCompra_cantidadInvalida_lanzaNegocioException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario("ana@correo.com", "hash")));
        when(juegoRepository.findById(5L)).thenReturn(Optional.of(juego(5, "58.00")));
        assertThrows(NegocioException.class, () -> service.crearCompra(1L, pedido(5, 0, null)));
    }

    @Test
    void crearCompra_juegoInexistente_lanzaNegocioException() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario("ana@correo.com", "hash")));
        when(juegoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NegocioException.class, () -> service.crearCompra(1L, pedido(99, 1, null)));
    }
}
