package com.restaurante.pedidos.dominio.servicio.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// DTO simple para productos en UI
record ProductoUIDTO(Long id, String nombre, BigDecimal precio, String categoria, boolean disponible) {}

public interface IServicioProductosUI {

    CompletableFuture<List<ProductoUIDTO>> obtenerProductosDisponibles();

    CompletableFuture<List<ProductoUIDTO>> buscarProductos(String texto);

    CompletableFuture<Boolean> actualizarDisponibilidadProducto(Long productoId, boolean disponible);
}