package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.EstadoPedidoDTO;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ControladorCocinero - Pruebas de lógica con mocks")
class ControladorCocineroTest {

    private ControladorCocinero controlador;
    private IServicioPedidosUI mockServicio;

    @BeforeEach
    void setUp() {
        controlador = new ControladorCocinero();
        mockServicio = mock(IServicioPedidosUI.class);
        controlador.setServicioPedidos(mockServicio);
    }

    @Test
    @DisplayName("Al marcar como listo, llama al servicio con parámetros correctos")
    void marcarComoListo_LlamaServicio_ConParametrosCorrectos() {
        PedidoDTO pedidoTest = crearPedidoMock(99L, "PED-TEST", CodigoEstadoPedido.EN_PREPARACION);

        when(mockServicio.transicionarEstado(eq(99L), eq(CodigoEstadoPedido.LISTO), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(true));

        // marcarComoListo es package-private → accesible desde el mismo paquete de test
        controlador.marcarComoListo(pedidoTest);

        verify(mockServicio).transicionarEstado(
                eq(99L),
                eq(CodigoEstadoPedido.LISTO),
                anyLong()
        );
    }

    @Test
    @DisplayName("Cuando servicio falla, controlador no lanza excepción no controlada")
    void marcarComoListo_CuandoServicioFalla_ManejaErrorGracefully() {
        PedidoDTO pedidoTest = crearPedidoMock(1L, "PED-001", CodigoEstadoPedido.EN_PREPARACION);

        when(mockServicio.transicionarEstado(anyLong(), any(), anyLong()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("BD caída")));

        assertDoesNotThrow(() -> controlador.marcarComoListo(pedidoTest));
        verify(mockServicio).transicionarEstado(anyLong(), any(), anyLong());
    }

    @Test
    @DisplayName("Cuando servicio retorna false, no lanza excepción")
    void marcarComoListo_CuandoServicioRetornaFalse_ManejaGracefully() {
        PedidoDTO pedidoTest = crearPedidoMock(1L, "PED-001", CodigoEstadoPedido.EN_PREPARACION);

        when(mockServicio.transicionarEstado(anyLong(), any(), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(false));

        assertDoesNotThrow(() -> controlador.marcarComoListo(pedidoTest));
    }

    @Test
    @DisplayName("setServicioPedidos - inyección funciona correctamente")
    void setServicioPedidos_InyeccionFunciona() {
        IServicioPedidosUI nuevoMock = mock(IServicioPedidosUI.class);
        assertDoesNotThrow(() -> controlador.setServicioPedidos(nuevoMock));
    }

    // === Helper ===

    // ✅ EstadoPedidoDTO.codigo es String → usamos .name()
    private PedidoDTO crearPedidoMock(Long id, String codigo, CodigoEstadoPedido estado) {
        return new PedidoDTO(
                id, codigo, "Cliente Test", "Dir Test", List.of(),
                new EstadoPedidoDTO(
                        id * 10,
                        estado.name(),          // "EN_PREPARACION", "LISTO", etc.
                        estado.name().toLowerCase().replace("_", " "),
                        Instant.now(),
                        "Test"
                ),
                BigDecimal.TEN,
                Instant.now(), null,
                false, null, 1L
        );
    }
}
