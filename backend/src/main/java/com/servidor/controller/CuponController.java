package com.servidor.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.servidor.dto.CuponRequest;
import com.servidor.model.Cupon;
import com.servidor.service.CompraService;
import com.servidor.service.NegocioException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    private final CompraService compraService;

    public CuponController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validar(@Valid @RequestBody CuponRequest request) {
        Map<String, Object> body = new HashMap<>();
        try {
            Cupon cupon = compraService.validarCupon(request.getCodigo());
            body.put("valido", true);
            body.put("codigo", cupon.getCodigo());
            body.put("porcentaje", cupon.getPorcentajeDescuento());
            return ResponseEntity.ok(body);
        } catch (NegocioException e) {
            body.put("valido", false);
            body.put("error", e.getMessage());
            return ResponseEntity.ok(body); // 200 con valido:false para que el frontend lo maneje facil
        }
    }
}
