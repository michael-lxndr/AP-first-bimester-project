package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;
import com.restaurante.pedidos.mocks.MockServicioPedidos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@DisplayName("ControladorCocinero - Pruebas de integración con mocks")
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
        // Arrange
        PedidoDTO pedidoTest = new PedidoDTO(
                99L, "PED-TEST", "Cliente", "Dir", List.of(),
                new com.restaurante.pedidos.dominio.dto.EstadoPedidoDTO(
                        1L, CodigoEstadoPedido.EN_PREPARACION, "En cocina", java.time.Instant.now(), "Cocina"
                ),
                java.math.BigDecimal.TEN, java.time.Instant.now(), null, false, null, 1L
        );

        // Mock: servicio retorna éxito
        when(mockServicio.transicionarEstado(eq(99L), eq(CodigoEstadoPedido.LISTO), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(true));

        // Act
        // Nota: En pruebas reales con TestFX, se simularía el click en el botón.
        // Aquí probamos la lógica directa del método.
        controlador.marcarComoListo(pedidoTest);

        // Esperar que la tarea asíncrona termine (mock es inmediato)
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        verify(mockServicio).transicionarEstado(
                eq(99L),
                eq(CodigoEstadoPedido.LISTO),
                anyLong()  // El ID del personal puede variar
        );
    }

    @Test
    @DisplayName("Cuando servicio falla, controlador maneja error sin crashear")
    void marcarComoListo_CuandoServicioFalla_ManejaErrorGracefully() {
        PedidoDTO pedidoTest = crearPedidoMock();

        when(mockServicio.transicionarEstado(anyLong(), any(), anyLong()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("BD caída")));

        // No debe lanzar excepción no controlada
        assertDoesNotThrow(() -> controlador.marcarComoListo(pedidoTest));

        WaitForAsyncUtils.waitForFxEvents();
        verify(mockServicio).transicionarEstado(anyLong(), any(), anyLong());
    }

    private PedidoDTO crearPedidoMock() {
        return new PedidoDTO(
                1L, "PED-001", "Test", "Test", List.of(),
                new com.restaurante.pedidos.dominio.dto.EstadoPedidoDTO(
                        1L, CodigoEstadoPedido.EN_PREPARACION, "En cocina", java.time.Instant.now(), "Test"
                ),
                java.math.BigDecimal.TEN, java.time.Instant.now(), null, false, null, 1L
        );
    }
}