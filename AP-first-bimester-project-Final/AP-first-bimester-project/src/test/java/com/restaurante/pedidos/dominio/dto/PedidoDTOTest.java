package com.restaurante.pedidos.dominio.dto;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PedidoDTO - Pruebas de inmutabilidad y helpers de UI")
class PedidoDTOTest {

    // ✅ EstadoPedidoDTO.codigo ahora es String, no enum
    private EstadoPedidoDTO estadoPendiente() {
        return new EstadoPedidoDTO(
                1L,
                CodigoEstadoPedido.PENDIENTE.name(),  // "PENDIENTE"
                "Pendiente",
                Instant.now(),
                "Sistema"
        );
    }

    private PedidoDTO crearPedidoEjemplo() {
        return new PedidoDTO(
                1L, "PED-TEST", "Cliente Test", "Dirección Test",
                List.of(
                        new ItemPedidoDTO(10L, "Producto A", 2, BigDecimal.TEN, BigDecimal.valueOf(20), null)
                ),
                estadoPendiente(),
                BigDecimal.valueOf(20),
                Instant.now(),
                Instant.now().plusSeconds(1800),
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
                new EstadoPedidoDTO(1L, CodigoEstadoPedido.PENDIENTE.name(), "Pendiente", ahora, "Sys"),
                BigDecimal.TEN, ahora, entrega, false, null, 1L
        );

        String resultado = dto.getTiempoRestanteParaUI();
        assertTrue(resultado.contains("min"), "Debe contener 'min': " + resultado);
    }

    @Test
    @DisplayName("getTiempoRestanteParaUI - Cuando entrega ya pasó, retorna 'Entregado'")
    void getTiempoRestante_CuandoEntregaPasada_RetornaEntregado() {
        Instant ahora = Instant.now();
        Instant entrega = ahora.minusSeconds(60);

        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "Cliente", "Dir", List.of(),
                new EstadoPedidoDTO(1L, CodigoEstadoPedido.PENDIENTE.name(), "Pendiente", ahora, "Sys"),
                BigDecimal.TEN, ahora, entrega, false, null, 1L
        );

        assertEquals("Entregado", dto.getTiempoRestanteParaUI());
    }

    @Test
    @DisplayName("getTiempoRestanteParaUI - Sin fecha de entrega retorna guion")
    void getTiempoRestante_SinFechaEntrega_RetornaGuion() {
        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "Cliente", "Dir", List.of(),
                estadoPendiente(),
                BigDecimal.TEN, Instant.now(), null, false, null, 1L
        );

        assertEquals("—", dto.getTiempoRestanteParaUI());
    }

    @Test
    @DisplayName("Constructor - Lista de items es inmutable (defensive copy)")
    void constructor_ItemsListaEsInmutable() {
        var itemsOriginales = List.of(
                new ItemPedidoDTO(1L, "A", 1, BigDecimal.ONE, BigDecimal.ONE, null)
        );

        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "C", "D", itemsOriginales,
                estadoPendiente(),
                BigDecimal.ONE, Instant.now(), null, false, null, 1L
        );

        assertThrows(UnsupportedOperationException.class, () ->
                dto.items().add(new ItemPedidoDTO(2L, "B", 1, BigDecimal.ONE, BigDecimal.ONE, null))
        );
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

    @Test
    @DisplayName("getCssClassParaEstado - Convierte guion bajo a guion en CSS")
    void getCssClassParaEstado_ConvierteGuionBajoAGuion() {
        PedidoDTO dto = new PedidoDTO(
                1L, "PED-001", "C", "D", List.of(),
                new EstadoPedidoDTO(1L, CodigoEstadoPedido.EN_PREPARACION.name(), "En cocina", Instant.now(), "Sys"),
                BigDecimal.TEN, Instant.now(), null, false, null, 1L
        );

        // "EN_PREPARACION" → "estado-en-preparacion"
        assertEquals("estado-en-preparacion", dto.getCssClassParaEstado());
    }

    @Test
    @DisplayName("Constructor - Total negativo lanza excepción")
    void constructor_TotalNegativo_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                new PedidoDTO(
                        1L, "PED-001", "C", "D", List.of(),
                        estadoPendiente(),
                        BigDecimal.valueOf(-1), Instant.now(), null, false, null, 1L
                )
        );
    }
}
