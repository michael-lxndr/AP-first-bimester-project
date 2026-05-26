package com.restaurante.pedidos.dominio.modelos;

import java.math.BigDecimal;

/**
 * Representa un producto a nivel de dominio.
 */
public record Producto(
    Long id,
    String codigo,
    String nombre,
    BigDecimal precio,
    Integer tiempoPreparacionMinutos
) {}
