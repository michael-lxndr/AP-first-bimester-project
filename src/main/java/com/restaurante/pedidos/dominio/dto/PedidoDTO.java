package com.restaurante.pedidos.dominio.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

/**
 * DTO principal para la UI de pedidos.
 * Contiene solo lo que la vista necesita mostrar o editar.
 * INMUTABLE: thread-safe por diseño.
 */
public record PedidoDTO(
        Long id,
        String codigoPedido,           // Para consulta del cliente: "PED-ABC123"
        String nombreCliente,          // Denormalizado (snapshot)
        String direccionEntrega,       // Snapshot: no objeto Direccion complejo
        List<ItemPedidoDTO> items,     // Lista inmutable
        EstadoPedidoDTO estadoActual,
        BigDecimal total,
        Instant creadoEn,
        Instant entregaEstimada,
        boolean prioritario,
        String notasVisibles,          // Solo notas que el personal debe ver
        Long asignadoAPersonalId       // Para saber quién lo está atendiendo
) {
    public PedidoDTO {
        // Defensive copy: evita que modifiquen la lista desde fuera
        if (items != null) {
            items = Collections.unmodifiableList(items);
        } else {
            items = Collections.emptyList();
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total inválido");
        }
    }

    // === MÉTODOS HELPER PARA UI (sin lógica de negocio) ===

    /**
     * Calcula tiempo restante para entrega (solo presentación).
     * No afecta reglas de negocio ni estados.
     */
    public String getTiempoRestanteParaUI() {
        if (entregaEstimada == null) return "—";

        long minutos = ChronoUnit.MINUTES.between(Instant.now(), entregaEstimada);

        if (minutos > 60) {
            return (minutos / 60) + "h " + (minutos % 60) + "min";
        } else if (minutos > 0) {
            return minutos + " min";
        } else {
            return "Entregado";
        }
    }

    /**
     * Determina clase CSS para el estado (para styling dinámico).
     */
    public String getCssClassParaEstado() {
        if (estadoActual == null) return "estado-desconocido";
        return "estado-" + estadoActual.codigo().name().toLowerCase().replace("_", "-");
    }

    /**
     * Factory method desde entidad (se migrará a servicio después).
     */
    public static PedidoDTO fromEntity(
            com.restaurante.pedidos.dominio.entidad.PedidoCliente entidad
    ) {
        if (entidad == null) return null;

        List<ItemPedidoDTO> itemsDTO = entidad.getItems() != null
                ? entidad.getItems().stream()
                .map(ItemPedidoDTO::fromEntity)
                .filter(java.util.Objects::nonNull)
                .toList()
                : List.of();

        return new PedidoDTO(
                entidad.getId(),
                entidad.getCodigoPedido(),
                entidad.getCliente() != null ? entidad.getCliente().getNombre() : "Cliente anónimo",
                entidad.getDireccionEntrega(),
                itemsDTO,
                EstadoPedidoDTO.fromEntity(entidad.getEstadoActual()),
                entidad.calcularTotal(),  // Método de dominio, OK
                entidad.getFechaCreacion(),
                entidad.getEntregaEstimada(),
                entidad.isPrioritario(),
                entidad.getNotasParaPersonal(),
                entidad.getAsignadoA() != null ? entidad.getAsignadoA().getId() : null
        );
    }
}