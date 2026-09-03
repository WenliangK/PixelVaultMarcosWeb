package com.servidor.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servidor.dto.AuthRequest;
import com.servidor.model.Usuario;
import com.servidor.service.NegocioException;
import com.servidor.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody AuthRequest request) {
        try {
            Usuario usuario = usuarioService.registrar(request.getUsername(), request.getPassword());
            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Usuario creado exitosamente");
            body.put("id", usuario.getId());
            return ResponseEntity.ok(body);
        } catch (NegocioException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            String token = usuarioService.autenticar(request.getUsername(), request.getPassword());
            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Login exitoso");
            body.put("token", token);
            return ResponseEntity.ok(body);
        } catch (NegocioException e) {
            return error(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private ResponseEntity<?> error(HttpStatus status, String mensaje) {
        Map<String, String> body = new HashMap<>();
        body.put("error", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
