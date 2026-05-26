package com.restaurante.pedidos.dominio.dto;

import com.restaurante.pedidos.clases.PedidosCliente;
import com.restaurante.pedidos.clases.Clientes;
import com.restaurante.pedidos.clases.DireccionesCliente;

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
        if (estadoActual.codigo() == null || estadoActual.codigo().isBlank()) {
            return "estado-desconocido";
        }
        return "estado-" + estadoActual.codigo().toLowerCase().replace("_", "-");
    }

    /**
     * Factory method desde entidad.
     */
    public static PedidoDTO fromEntity(PedidosCliente entidad) {
        if (entidad == null) return null;

        // Convertir items
        List<ItemPedidoDTO> itemsDTO = entidad.getItemsPedidoCollection() != null
                ? entidad.getItemsPedidoCollection().stream()
                .map(ItemPedidoDTO::fromEntity)
                .filter(java.util.Objects::nonNull)
                .toList()
                : List.of();

        // Obtener nombre del cliente
        String nombreCliente = "Cliente anónimo";
        Clientes cliente = entidad.getClienteId();
        if (cliente != null && cliente.getNombreCompleto() != null) {
            nombreCliente = cliente.getNombreCompleto();
        }

        // Obtener dirección de entrega
        String direccionEntrega = entidad.getSnapshotDireccionEntrega();
        if (direccionEntrega == null || direccionEntrega.isBlank()) {
            DireccionesCliente direccion = entidad.getDireccionEntregaId();
            if (direccion != null) {
                // Construir dirección a partir de los campos
                direccionEntrega = String.format("%s %s, %s, %s, %s",
                        direccion.getCallePrincipal() != null ? direccion.getCallePrincipal() : "",
                        direccion.getNumeroCasa() != null ? direccion.getNumeroCasa() : "",
                        direccion.getCiudad() != null ? direccion.getCiudad() : "",
                        direccion.getProvincia() != null ? direccion.getProvincia() : "",
                        direccion.getPais() != null ? direccion.getPais() : ""
                ).replaceAll("\\s+", " ").trim();
            }
        }

        // Convertir Date a Instant
        Instant creadoEnInstant = null;
        if (entidad.getCreadoEn() != null) {
            creadoEnInstant = entidad.getCreadoEn().toInstant();
        }

        Instant entregaEstimadaInstant = null;
        if (entidad.getEntregaEstimadaEn() != null) {
            entregaEstimadaInstant = entidad.getEntregaEstimadaEn().toInstant();
        }

        // Obtener ID del personal asignado (de Entregas)
        Long asignadoAPersonalId = null;
        if (entidad.getEntregas() != null && entidad.getEntregas().getRepartidorPersonalId() != null) {
            asignadoAPersonalId = entidad.getEntregas().getRepartidorPersonalId().getPersonalId();
        }

        return new PedidoDTO(
                entidad.getPedidoId(),
                entidad.getCodigoPedido(),
                nombreCliente,
                direccionEntrega,
                itemsDTO,
                EstadoPedidoDTO.fromEntity(entidad.getEstadoActualId()),
                entidad.getTotal(),
                creadoEnInstant,
                entregaEstimadaInstant,
                entidad.getPrioritario(),
                entidad.getNotasGenerales(),
                asignadoAPersonalId
        );
    }
}
