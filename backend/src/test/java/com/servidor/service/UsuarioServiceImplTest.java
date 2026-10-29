package com.servidor.service;

import com.servidor.model.Usuario;
import com.servidor.repository.UsuarioRepository;
import com.servidor.security.JwtUtil;
import com.servidor.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de registro y autenticación (RF-01, RNF-06). */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtUtil jwtUtil;

    private UsuarioServiceImpl service;

    @BeforeEach
    void preparar() {
        service = new UsuarioServiceImpl(usuarioRepository, jwtUtil);
    }

    @Test
    void registrar_usuarioExistente_lanzaNegocioException() {
        when(usuarioRepository.existsByUsername("ana@correo.com")).thenReturn(true);
        assertThrows(NegocioException.class, () -> service.registrar("ana@correo.com", "secreto123"));
    }

    @Test
    void registrar_guardaElHashBCryptYNoLaContrasenaEnClaro() {
        when(usuarioRepository.existsByUsername("ana@correo.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        service.registrar("ana@correo.com", "secreto123");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertNotEquals("secreto123", captor.getValue().getPassword());
        assertTrue(captor.getValue().getPassword().startsWith("$2"), "debe ser un hash BCrypt");
    }

    @Test
    void autenticar_usuarioInexistente_lanzaNegocioException() {
        when(usuarioRepository.findByUsername("nadie@correo.com")).thenReturn(Optional.empty());
        assertThrows(NegocioException.class, () -> service.autenticar("nadie@correo.com", "x"));
    }

    @Test
    void autenticar_credencialesCorrectas_devuelveToken() {
        // Se registra primero para obtener un hash BCrypt real y luego se autentica con el mismo.
        when(usuarioRepository.existsByUsername("ana@correo.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        Usuario guardado = service.registrar("ana@correo.com", "secreto123");
        guardado.setId(7L);

        when(usuarioRepository.findByUsername("ana@correo.com")).thenReturn(Optional.of(guardado));
        when(jwtUtil.generarToken(7L, "ana@correo.com")).thenReturn("token-de-prueba");

        assertEquals("token-de-prueba", service.autenticar("ana@correo.com", "secreto123"));
    }

    @Test
    void autenticar_contrasenaIncorrecta_lanzaNegocioException() {
        when(usuarioRepository.existsByUsername("ana@correo.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        Usuario guardado = service.registrar("ana@correo.com", "secreto123");
        when(usuarioRepository.findByUsername("ana@correo.com")).thenReturn(Optional.of(guardado));

        assertThrows(NegocioException.class, () -> service.autenticar("ana@correo.com", "incorrecta"));
    }
}
