package com.restaurante.pedidos.dominio;

/**
 * Enum canónico de roles para la capa de presentación y servicios.
 * Espeja los valores del enum de persistencia (Clases.Enums.CodigoRol)
 * para mantener independencia entre capas.
 */
public enum CodigoRol {
    ADMINISTRADOR,
    COCINERO,
    REPARTIDOR
}
