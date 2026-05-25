package com.restaurante.pedidos.dominio.servicio.ui;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Contrato de servicios para la capa de presentación.
 *
 * Reglas:
 * - Todos los métodos retornan DTOs, NUNCA entidades JPA.
 * - Operaciones de lectura son asíncronas (CompletableFuture).
 * - Operaciones de escritura retornan boolean/Optional para manejo de errores en UI.
 */
public interface IServicioPedidosUI {

    // === CONSULTAS (Asíncronas - no bloquean UI) ===

    /**
     * Obtiene pedidos filtrados por estado.
     * Ej: Cocinero ve solo EN_PREPARACION.
     */
    CompletableFuture<List<PedidoDTO>> obtenerPedidosPorEstado(CodigoEstadoPedido estado);

    /**
     * Consulta rápida por código (para cliente).
     */
    CompletableFuture<Optional<PedidoDTO>> consultarPorCodigo(String codigo);

    /**
     * Obtiene un pedido por ID (para edición/admin).
     */
    CompletableFuture<Optional<PedidoDTO>> obtenerPedidoPorId(Long id);

    // === ACCIONES (Asíncronas con resultado) ===

    /**
     * Transiciona el estado de un pedido.
     * @return true si la transición fue válida y exitosa
     */
    CompletableFuture<Boolean> transicionarEstado(
            Long pedidoId,
            CodigoEstadoPedido nuevoEstado,
            Long personalId
    );

    /**
     * Asigna un pedido a un repartidor/cocinero.
     */
    CompletableFuture<Boolean> asignarPersonal(Long pedidoId, Long personalId);

    /**
     * Cancela un pedido (solo si está en estado permitido).
     */
    CompletableFuture<Boolean> cancelarPedido(Long pedidoId, Long personalId, String motivo);
}