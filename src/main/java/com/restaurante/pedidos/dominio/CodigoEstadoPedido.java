package com.restaurante.pedidos.dominio;

/**
 * Enum canónico de estados de pedido para la capa de presentación y servicios.
 * Espeja los valores del enum de persistencia (Clases.Enums.CodigoEstadoPedido)
 * para mantener independencia entre capas.
 */
public enum CodigoEstadoPedido {
    PENDIENTE,
    EN_PREPARACION,
    LISTO,
    EN_CAMINO,
    ENTREGADO
}
