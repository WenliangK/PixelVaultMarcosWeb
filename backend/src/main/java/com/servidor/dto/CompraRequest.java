package com.servidor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CompraRequest {

    @NotEmpty(message = "El carrito no puede estar vacio")
    @Valid
    private List<ItemCompraRequest> items;

    private String cuponCodigo; // opcional

    public List<ItemCompraRequest> getItems() { return items; }
    public void setItems(List<ItemCompraRequest> items) { this.items = items; }

    public String getCuponCodigo() { return cuponCodigo; }
    public void setCuponCodigo(String cuponCodigo) { this.cuponCodigo = cuponCodigo; }

    public static class ItemCompraRequest {
        private Long juegoId;
        private Integer cantidad;

        public Long getJuegoId() { return juegoId; }
        public void setJuegoId(Long juegoId) { this.juegoId = juegoId; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
