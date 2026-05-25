package com.restaurante.pedidos.dominio.modelos;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representa un pedido a nivel de dominio.
 */
public record Pedido(
    Long id,
    String codigoPedido,
    String nombreCliente,
    String direccionEntrega,
    EstadoPedido estadoActual,
    BigDecimal total,
    Instant creadoEn
) {}
