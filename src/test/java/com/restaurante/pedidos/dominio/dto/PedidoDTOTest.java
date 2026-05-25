package com.restaurante.pedidos.dominio.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PedidoDTO - Pruebas de inmutabilidad y helpers de UI")
class PedidoDTOTest {

    private PedidoDTO crearPedidoEjemplo() {
        return new PedidoDTO(
                1L, "PED-TEST", "Cliente Test", "Dirección Test",
                List.of(
                        new ItemPedidoDTO(10L, "Producto A", 2, BigDecimal.TEN, BigDecimal.valueOf(20), null)
                ),
                new EstadoPedidoDTO(100L, com.restaurante.pedidos.dominio.CodigoEstadoPedido.RECIBIDO, "Recibido", Instant.now(), "Sistema"),
                BigDecimal.valueOf(20),
                Instant.now(),
                Instant.now().plusSeconds(1800),  // +30 min
                false,
                "Nota de prueba",
                1L
        );
    }

    @Test
    @DisplayName("getTiempoRestanteParaUI - Cuando entrega es futura, retorna minutos")
    void getTiempoRestante_CuandoEntregaFutura_RetornaMinutos() {
        Instant ahora = Instant.now();
        Instant entrega = ahora.plusSeconds(900); // +15 min

        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "Cliente", "Dir", List.of(),
                new EstadoPedidoDTO(1L, com.restaurante.pedidos.dominio.CodigoEstadoPedido.RECIBIDO, "Recibido", ahora, "Sys"),
                BigDecimal.TEN, ahora, entrega, false, null, 1L
        );

        String resultado = dto.getTiempoRestanteParaUI();
        assertTrue(resultado.contains("min"), "Debe contener 'min': " + resultado);
    }

    @Test
    @DisplayName("getTiempoRestanteParaUI - Cuando entrega ya pasó, retorna 'Entregado'")
    void getTiempoRestante_CuandoEntregaPasada_RetornaEntregado() {
        Instant ahora = Instant.now();
        Instant entrega = ahora.minusSeconds(60); // -1 min

        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "Cliente", "Dir", List.of(),
                new EstadoPedidoDTO(1L, com.restaurante.pedidos.dominio.CodigoEstadoPedido.RECIBIDO, "Recibido", ahora, "Sys"),
                BigDecimal.TEN, ahora, entrega, false, null, 1L
        );

        assertEquals("Entregado", dto.getTiempoRestanteParaUI());
    }

    @Test
    @DisplayName("Constructor - Lista de items es inmutable (defensive copy)")
    void constructor_ItemsListaEsInmutable() {
        var itemsOriginales = List.of(
                new ItemPedidoDTO(1L, "A", 1, BigDecimal.ONE, BigDecimal.ONE, null)
        );

        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "C", "D", itemsOriginales,
                new EstadoPedidoDTO(1L, com.restaurante.pedidos.dominio.CodigoEstadoPedido.RECIBIDO, "R", Instant.now(), "S"),
                BigDecimal.ONE, Instant.now(), null, false, null, 1L
        );

        // Intentar modificar la lista interna debe fallar
        assertThrows(UnsupportedOperationException.class, () -> {
            dto.items().add(new ItemPedidoDTO(2L, "B", 1, BigDecimal.ONE, BigDecimal.ONE, null));
        });
    }

    @Test
    @DisplayName("getCssClassParaEstado - Genera clase CSS válida para styling")
    void getCssClassParaEstado_GeneraClaseValida() {
        PedidoDTO dto = crearPedidoEjemplo();
        String cssClass = dto.getCssClassParaEstado();

        assertNotNull(cssClass);
        assertTrue(cssClass.startsWith("estado-"), "Debe empezar con 'estado-': " + cssClass);
        assertFalse(cssClass.contains(" "), "No debe tener espacios: " + cssClass);
    }
}