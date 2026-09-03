package com.servidor.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servidor.dto.CompraRequest;
import com.servidor.model.Compra;
import com.servidor.service.CompraService;
import com.servidor.service.NegocioException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<?> crearCompra(@Valid @RequestBody CompraRequest request, HttpServletRequest httpRequest) {
        Long usuarioId = (Long) httpRequest.getAttribute("usuarioId");
        try {
            Compra compra = compraService.crearCompra(usuarioId, request);
            return ResponseEntity.ok(compra);
        } catch (NegocioException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    @GetMapping("/historial")
    public List<Compra> historial(HttpServletRequest httpRequest) {
        Long usuarioId = (Long) httpRequest.getAttribute("usuarioId");
        return compraService.historialPorUsuario(usuarioId);
    }
}
