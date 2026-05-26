package com.restaurante.pedidos.mocks;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementación mock de IServicioPedidosUI para desarrollo de UI.
 *
 * Características:
 * - Datos predecibles para pruebas manuales.
 * - Simula latencia de red/BD (100-300ms) para testear estados de carga.
 * - Fácil de reemplazar: cuando Persona 2 entregue el servicio real,
 *   solo cambia la inyección en el controlador.
 */
public class MockServicioPedidos implements IServicioPedidosUI {

    // Datos de prueba: en producción vendrían de BD
    private static final List<PedidoDTO> PEDIDOS_DE_PRUEBA = generarPedidosMock();

    private static List<PedidoDTO> generarPedidosMock() {
        List<PedidoDTO> pedidos = new ArrayList<>();

        // Pedido 1: En preparación (para cocinero)
        pedidos.add(new PedidoDTO(
                1L, "PED-001", "María López", "Calle Falsa 123",
                List.of(
                        new ItemPedidoDTO(10L, "Hamburguesa Clásica", 2, BigDecimal.valueOf(8500), BigDecimal.valueOf(17000), "Sin cebolla"),
                        new ItemPedidoDTO(15L, "Papas Fritas", 1, BigDecimal.valueOf(4500), BigDecimal.valueOf(4500), null)
                ),
                new EstadoPedidoDTO(100L, CodigoEstadoPedido.EN_PREPARACION, "En cocina", Instant.now().minusSeconds(120), "Ana (Cocina)"),
                BigDecimal.valueOf(21500),
                Instant.now().minusMinutes(10),
                Instant.now().plusMinutes(15),
                false,
                "Cliente alérgico al maní",
                2L
        ));

        // Pedido 2: Listo para entrega (para repartidor)
        pedidos.add(new PedidoDTO(
                2L, "PED-002", "Carlos Ruiz", "Av. Siempre Viva 742",
                List.of(new ItemPedidoDTO(20L, "Pizza Margarita", 1, BigDecimal.valueOf(15000), BigDecimal.valueOf(15000), "Extra queso")),
                new EstadoPedidoDTO(101L, CodigoEstadoPedido.LISTO, "Listo para retirar", Instant.now().minusSeconds(30), "Ana (Cocina)"),
                BigDecimal.valueOf(15000),
                Instant.now().minusMinutes(25),
                Instant.now().plusMinutes(5),
                true,  // prioritario
                null,
                3L
        ));

        // Pedido 3: Entregado (histórico)
        pedidos.add(new PedidoDTO(
                3L, "PED-003", "Laura Gómez", "Calle 10 #5-20",
                List.of(new ItemPedidoDTO(12L, "Ensalada César", 1, BigDecimal.valueOf(12000), BigDecimal.valueOf(12000), null)),
                new EstadoPedidoDTO(102L, CodigoEstadoPedido.ENTREGADO, "Entregado", Instant.now().minusMinutes(30), "Luis (Repartidor)"),
                BigDecimal.valueOf(12000),
                Instant.now().minusHours(1),
                Instant.now().minusMinutes(30),
                false,
                null,
                3L
        ));

        return Collections.unmodifiableList(pedidos);
    }

    @Override
    public CompletableFuture<List<PedidoDTO>> obtenerPedidosPorEstado(CodigoEstadoPedido estado) {
        return CompletableFuture.supplyAsync(() -> {
            // Simular latencia de BD (100-300ms)
            simularLatencia();

            return PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> p.estadoActual() != null && p.estadoActual().codigo() == estado)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Optional<PedidoDTO>> consultarPorCodigo(String codigo) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();

            return PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> codigo.equalsIgnoreCase(p.codigoPedido()))
                    .findFirst();
        });
    }

    @Override
    public CompletableFuture<Optional<PedidoDTO>> obtenerPedidoPorId(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            return PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> Objects.equals(p.id(), id))
                    .findFirst();
        });
    }

    @Override
    public CompletableFuture<Boolean> transicionarEstado(Long pedidoId, CodigoEstadoPedido nuevoEstado, Long personalId) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();

            // Lógica mock: permitir transiciones válidas
            var pedido = PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> Objects.equals(p.id(), pedidoId))
                    .findFirst()
                    .orElse(null);

            if (pedido == null) return false;

            // Reglas simples de mock (en producción, esto va en dominio)
            boolean transicionValida = switch (pedido.estadoActual().codigo()) {
                case RECIBIDO -> nuevoEstado == CodigoEstadoPedido.EN_PREPARACION;
                case EN_PREPARACION -> nuevoEstado == CodigoEstadoPedido.LISTO;
                case LISTO -> nuevoEstado == CodigoEstadoPedido.EN_REPARTO;
                case EN_REPARTO -> nuevoEstado == CodigoEstadoPedido.ENTREGADO;
                default -> false;
            };

            return transicionValida;
        });
    }

    @Override
    public CompletableFuture<Boolean> asignarPersonal(Long pedidoId, Long personalId) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            // Mock: siempre éxito si los IDs son positivos
            return pedidoId > 0 && personalId > 0;
        });
    }

    @Override
    public CompletableFuture<Boolean> cancelarPedido(Long pedidoId, Long personalId, String motivo) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            // Mock: solo se puede cancelar si está en RECIBIDO
            var pedido = PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> Objects.equals(p.id(), pedidoId))
                    .findFirst();
            return pedido.map(p -> p.estadoActual().codigo() == CodigoEstadoPedido.RECIBIDO)
                    .orElse(false);
        });
    }

    private void simularLatencia() {
        try {
            Thread.sleep(new Random().nextInt(200) + 100); // 100-300ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}