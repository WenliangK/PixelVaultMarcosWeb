package com.servidor.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de las anotaciones de validación (Bean Validation / Hibernate Validator) de los DTO. */
class ValidacionDtoTest {

    private static ValidatorFactory fabrica;
    private static Validator validador;

    @BeforeAll
    static void iniciar() {
        fabrica = Validation.buildDefaultValidatorFactory();
        validador = fabrica.getValidator();
    }

    @AfterAll
    static void cerrar() {
        fabrica.close();
    }

    private AuthRequest auth(String usuario, String clave) {
        AuthRequest r = new AuthRequest();
        r.setUsername(usuario);
        r.setPassword(clave);
        return r;
    }

    @Test
    void authRequest_valido_noTieneErrores() {
        assertTrue(validador.validate(auth("ana@correo.com", "secreto123")).isEmpty());
    }

    @Test
    void authRequest_correoInvalido_tieneError() {
        Set<ConstraintViolation<AuthRequest>> errores = validador.validate(auth("no-es-correo", "secreto123"));
        assertFalse(errores.isEmpty());
    }

    @Test
    void authRequest_contrasenaCorta_tieneError() {
        assertFalse(validador.validate(auth("ana@correo.com", "123")).isEmpty());
    }

    @Test
    void compraRequest_carritoVacio_tieneError() {
        CompraRequest r = new CompraRequest();
        r.setItems(List.of());
        assertFalse(validador.validate(r).isEmpty());
    }

    @Test
    void cuponRequest_codigoEnBlanco_tieneError() {
        CuponRequest r = new CuponRequest();
        r.setCodigo("  ");
        assertFalse(validador.validate(r).isEmpty());
    }
}
