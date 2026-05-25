package com.restaurante.pedidos.mocks;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.EstadoPedidoDTO;
import com.restaurante.pedidos.dominio.dto.ItemPedidoDTO;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementación mock de IServicioPedidosUI para desarrollo de UI.
 * EstadoPedidoDTO.codigo es String ("PENDIENTE", "EN_PREPARACION", etc.)
 * IServicioPedidosUI usa CodigoEstadoPedido enum para filtrar/transicionar.
 */
public class MockServicioPedidos implements IServicioPedidosUI {

    private static final List<PedidoDTO> PEDIDOS_DE_PRUEBA = generarPedidosMock();

    private static List<PedidoDTO> generarPedidosMock() {
        List<PedidoDTO> pedidos = new ArrayList<>();

        // Pedido 1: En preparación (para cocinero)
        pedidos.add(new PedidoDTO(
                1L, "PED-001", "María López", "Calle Falsa 123",
                List.of(
                        new ItemPedidoDTO(10L, "Hamburguesa Clásica", 2, BigDecimal.valueOf(8500), BigDecimal.valueOf(17000), "Sin cebolla"),
                        new ItemPedidoDTO(15L, "Papas Fritas",        1, BigDecimal.valueOf(4500), BigDecimal.valueOf(4500), null)
                ),
                // ✅ codigo es String: usamos .name() del enum
                new EstadoPedidoDTO(100L, CodigoEstadoPedido.EN_PREPARACION.name(), "En cocina", Instant.now().minusSeconds(120), "Ana (Cocina)"),
                BigDecimal.valueOf(21500),
                Instant.now().minusSeconds(600),
                Instant.now().plusSeconds(900),
                false,
                "Cliente alérgico al maní",
                2L
        ));

        // Pedido 2: Listo para entrega (para repartidor)
        pedidos.add(new PedidoDTO(
                2L, "PED-002", "Carlos Ruiz", "Av. Siempre Viva 742",
                List.of(new ItemPedidoDTO(20L, "Pizza Margarita", 1, BigDecimal.valueOf(15000), BigDecimal.valueOf(15000), "Extra queso")),
                new EstadoPedidoDTO(101L, CodigoEstadoPedido.LISTO.name(), "Listo para retirar", Instant.now().minusSeconds(30), "Ana (Cocina)"),
                BigDecimal.valueOf(15000),
                Instant.now().minusSeconds(1500),
                Instant.now().plusSeconds(300),
                true,
                null,
                3L
        ));

        // Pedido 3: En camino (para repartidor)
        pedidos.add(new PedidoDTO(
                3L, "PED-003", "Laura Gómez", "Calle 10 #5-20",
                List.of(new ItemPedidoDTO(12L, "Ensalada César", 1, BigDecimal.valueOf(12000), BigDecimal.valueOf(12000), null)),
                new EstadoPedidoDTO(102L, CodigoEstadoPedido.EN_CAMINO.name(), "En camino", Instant.now().minusSeconds(300), "Luis (Repartidor)"),
                BigDecimal.valueOf(12000),
                Instant.now().minusSeconds(3600),
                Instant.now().plusSeconds(600),
                false,
                null,
                3L
        ));

        // Pedido 4: Pendiente (para cocinero)
        pedidos.add(new PedidoDTO(
                4L, "PED-004", "Roberto Torres", "Diagonal 80 #20",
                List.of(new ItemPedidoDTO(5L, "Pollo a la plancha", 2, BigDecimal.valueOf(9000), BigDecimal.valueOf(18000), null)),
                new EstadoPedidoDTO(103L, CodigoEstadoPedido.PENDIENTE.name(), "Pendiente", Instant.now().minusSeconds(60), "Sistema"),
                BigDecimal.valueOf(18000),
                Instant.now().minusSeconds(60),
                Instant.now().plusSeconds(1800),
                false,
                null,
                null
        ));

        return Collections.unmodifiableList(pedidos);
    }

    @Override
    public CompletableFuture<List<PedidoDTO>> obtenerPedidosPorEstado(CodigoEstadoPedido estado) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            // ✅ codigo() es String, estado.name() convierte el enum a String para comparar
            return PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> p.estadoActual() != null
                            && estado.name().equals(p.estadoActual().codigo()))
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

            var pedido = PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> Objects.equals(p.id(), pedidoId))
                    .findFirst()
                    .orElse(null);

            if (pedido == null) return false;

            // ✅ codigo() es String → switch con String
            boolean transicionValida = switch (pedido.estadoActual().codigo()) {
                case "PENDIENTE"      -> nuevoEstado == CodigoEstadoPedido.EN_PREPARACION;
                case "EN_PREPARACION" -> nuevoEstado == CodigoEstadoPedido.LISTO;
                case "LISTO"          -> nuevoEstado == CodigoEstadoPedido.EN_CAMINO;
                case "EN_CAMINO"      -> nuevoEstado == CodigoEstadoPedido.ENTREGADO;
                default               -> false;
            };

            return transicionValida;
        });
    }

    @Override
    public CompletableFuture<Boolean> asignarPersonal(Long pedidoId, Long personalId) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            return pedidoId > 0 && personalId > 0;
        });
    }

    @Override
    public CompletableFuture<Boolean> cancelarPedido(Long pedidoId, Long personalId, String motivo) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            var pedido = PEDIDOS_DE_PRUEBA.stream()
                    .filter(p -> Objects.equals(p.id(), pedidoId))
                    .findFirst();
            // ✅ comparar String con String
            return pedido.map(p -> CodigoEstadoPedido.PENDIENTE.name().equals(p.estadoActual().codigo()))
                    .orElse(false);
        });
    }

    private void simularLatencia() {
        try {
            Thread.sleep(new Random().nextInt(200) + 100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
