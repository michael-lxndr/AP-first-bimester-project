package com.restaurante.pedidos.dominio.dto;

import com.restaurante.pedidos.Clases.ItemsPedido;
import com.restaurante.pedidos.Clases.Productos;
import java.math.BigDecimal;

public record ItemPedidoDTO(
        Long productoId,
        String nombreProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String observaciones
) {
    public ItemPedidoDTO {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Precio inválido");
        }
    }

    public static ItemPedidoDTO fromEntity(ItemsPedido entidad) {
        if (entidad == null) return null;

        Productos producto = entidad.getProductoId();
        String nombreProducto = producto != null ? producto.getNombreProducto() : "Producto eliminado";
        Long productoId = producto != null ? producto.getProductoId() : null;

        return new ItemPedidoDTO(
                productoId,
                nombreProducto,
                entidad.getCantidad(),
                entidad.getPrecioUnitario(),
                entidad.getTotalLinea(),        // totalLinea = cantidad * precioUnitario
                entidad.getNotaEspecial()       // observaciones
        );
    }
}