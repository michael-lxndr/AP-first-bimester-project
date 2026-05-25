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

@DisplayName("ControladorRepartidor - Pruebas de lógica de transición")
class ControladorRepartidorTest {

    private ControladorRepartidor controlador;
    private IServicioPedidosUI mockServicio;

    @BeforeEach
    void setUp() {
        controlador = new ControladorRepartidor();
        mockServicio = mock(IServicioPedidosUI.class);
        controlador.setServicioPedidos(mockServicio);
    }

    @Test
    @DisplayName("iniciarEntrega - llama servicio con estado EN_CAMINO")
    void iniciarEntrega_LlamaServicio_ConEstadoCorrecto() {
        PedidoDTO pedido = crearPedidoMock(2L, "PED-002", CodigoEstadoPedido.LISTO);

        when(mockServicio.transicionarEstado(eq(2L), eq(CodigoEstadoPedido.EN_CAMINO), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(true));

        controlador.iniciarEntrega(pedido);

        verify(mockServicio).transicionarEstado(eq(2L), eq(CodigoEstadoPedido.EN_CAMINO), anyLong());
    }

    @Test
    @DisplayName("confirmarEntrega - llama servicio con estado ENTREGADO")
    void confirmarEntrega_LlamaServicio_ConEstadoCorrecto() {
        PedidoDTO pedido = crearPedidoMock(3L, "PED-003", CodigoEstadoPedido.EN_CAMINO);

        when(mockServicio.transicionarEstado(eq(3L), eq(CodigoEstadoPedido.ENTREGADO), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(true));

        controlador.confirmarEntrega(pedido);

        verify(mockServicio).transicionarEstado(eq(3L), eq(CodigoEstadoPedido.ENTREGADO), anyLong());
    }

    @Test
    @DisplayName("iniciarEntrega - cuando servicio falla, no lanza excepción no controlada")
    void iniciarEntrega_CuandoServicioFalla_ManejaErrorGracefully() {
        PedidoDTO pedido = crearPedidoMock(99L, "PED-ERR", CodigoEstadoPedido.LISTO);

        when(mockServicio.transicionarEstado(anyLong(), any(), anyLong()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Error de red")));

        assertDoesNotThrow(() -> controlador.iniciarEntrega(pedido));
        verify(mockServicio).transicionarEstado(anyLong(), any(), anyLong());
    }

    @Test
    @DisplayName("confirmarEntrega - cuando servicio retorna false, no lanza excepción")
    void confirmarEntrega_CuandoServicioRetornaFalse_ManejaGracefully() {
        PedidoDTO pedido = crearPedidoMock(5L, "PED-005", CodigoEstadoPedido.EN_CAMINO);

        when(mockServicio.transicionarEstado(anyLong(), any(), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(false));

        assertDoesNotThrow(() -> controlador.confirmarEntrega(pedido));
    }

    @Test
    @DisplayName("setServicioPedidos - inyección funciona correctamente")
    void setServicioPedidos_InyeccionFunciona() {
        IServicioPedidosUI nuevoMock = mock(IServicioPedidosUI.class);
        assertDoesNotThrow(() -> controlador.setServicioPedidos(nuevoMock));
    }

    // === Helper ===

    // ✅ EstadoPedidoDTO.codigo es String
    private PedidoDTO crearPedidoMock(Long id, String codigo, CodigoEstadoPedido estado) {
        return new PedidoDTO(
                id, codigo, "Cliente Test", "Dirección Test", List.of(),
                new EstadoPedidoDTO(
                        id * 10,
                        estado.name(),
                        estado.name().toLowerCase().replace("_", " "),
                        Instant.now(),
                        "Test"
                ),
                BigDecimal.valueOf(10000),
                Instant.now(), Instant.now().plusSeconds(1800),
                false, null, 2L
        );
    }
}
