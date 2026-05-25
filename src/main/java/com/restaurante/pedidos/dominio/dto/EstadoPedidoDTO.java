package com.restaurante.pedidos.dominio.dto;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import java.time.Instant;

/**
 * DTO inmutable para representar el estado de un pedido en la UI.
 * No contiene lógica de negocio, solo datos para presentación.
 */
public record EstadoPedidoDTO(
        Long id,
        CodigoEstadoPedido codigo,
        String descripcion,
        Instant fechaCambio,
        String responsableNombre  // Denormalizado: solo lo que la UI necesita
) {
    public EstadoPedidoDTO {
        // Validaciones de integridad (no de negocio)
        if (codigo == null) {
            throw new IllegalArgumentException("El código de estado no puede ser null");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
    }

    /**
     * Factory method: convierte entidad → DTO.
     * Este método DEBE estar en la capa de servicio, pero lo dejamos aquí
     * temporalmente para que Persona 3 pueda avanzar. Se migrará después.
     */
    public static EstadoPedidoDTO fromEntity(
            com.restaurante.pedidos.dominio.entidad.EstadoPedido entidad
    ) {
        if (entidad == null) return null;

        String responsable = entidad.getResponsable() != null
                ? entidad.getResponsable().getNombreCompleto()
                : "Sistema";

        return new EstadoPedidoDTO(
                entidad.getId(),
                entidad.getCodigoEstado(),
                entidad.getDescripcion(),
                entidad.getFechaCambio(),
                responsable
        );
    }
}