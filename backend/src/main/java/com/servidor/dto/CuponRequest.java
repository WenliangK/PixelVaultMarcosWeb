package com.servidor.dto;

import jakarta.validation.constraints.NotBlank;

public class CuponRequest {
    @NotBlank(message = "Ingresa un codigo de cupon")
    private String codigo;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}
