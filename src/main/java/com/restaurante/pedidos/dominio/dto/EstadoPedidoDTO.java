package com.restaurante.pedidos.dominio.dto;

import com.restaurante.pedidos.clases.EstadosPedido;

import java.time.Instant;

/**
 * DTO inmutable para representar el estado de un pedido en la UI.
 * No contiene lógica de negocio, solo datos para presentación.
 */
public record EstadoPedidoDTO(
        Long id,
        String codigo,           // Cambiado: el código viene como String de la BD
        String descripcion,
        Instant fechaCambio,
        String responsableNombre
) {
    public EstadoPedidoDTO {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de estado no puede ser null");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
    }

    /**
     * Factory method: convierte entidad → DTO.
     */
    public static EstadoPedidoDTO fromEntity(EstadosPedido entidad) {
        if (entidad == null) return null;

        String responsable = "Sistema";
        // Nota: EstadosPedido no tiene referencia directa a Personal
        // Si necesitas el responsable, tendrías que obtenerlo del HistorialEstadosPedido

        return new EstadoPedidoDTO(
                entidad.getEstadoId(),
                entidad.getCodigoEstado(),      // String: "PENDIENTE", "EN_PREPARACION", etc.
                entidad.getNombreEstado(),
                null,                           // EstadosPedido no tiene fechaCambio
                responsable
        );
    }
}
