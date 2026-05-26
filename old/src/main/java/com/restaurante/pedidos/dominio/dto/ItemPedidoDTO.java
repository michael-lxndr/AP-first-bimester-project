package com.restaurante.pedidos.dominio.dto;

import java.math.BigDecimal;

public record ItemPedidoDTO(
        Long productoId,
        String nombreProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String observaciones  // Ej: "Sin cebolla", "Extra queso"
) {
    public ItemPedidoDTO {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }
        // subtotal se calcula, pero lo recibimos ya calculado del servicio
    }

    public static ItemPedidoDTO fromEntity(
            com.restaurante.pedidos.dominio.entidad.ItemPedido entidad
    ) {
        if (entidad == null) return null;

        var producto = entidad.getProducto();
        return new ItemPedidoDTO(
                producto != null ? producto.getId() : null,
                producto != null ? producto.getNombre() : "Producto eliminado",
                entidad.getCantidad(),
                entidad.getPrecioUnitario(),
                entidad.getSubtotal(),
                entidad.getObservaciones()
        );
    }
}