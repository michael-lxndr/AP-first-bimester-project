package com.restaurante.pedidos.dominio.modelos;

/**
 * Representa los estados del pedido a nivel de dominio.
 */
public enum EstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    LISTO,
    EN_CAMINO,
    ENTREGADO
}
