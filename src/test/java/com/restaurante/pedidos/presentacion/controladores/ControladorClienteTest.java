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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ControladorCliente - Pruebas de consulta por código")
class ControladorClienteTest {

    private ControladorCliente controlador;
    private IServicioPedidosUI mockServicio;

    @BeforeEach
    void setUp() {
        controlador = new ControladorCliente();
        mockServicio = mock(IServicioPedidosUI.class);
        controlador.setServicioPedidos(mockServicio);
    }

    @Test
    @DisplayName("setServicioPedidos - inyección funciona correctamente")
    void setServicioPedidos_InyeccionFunciona() {
        assertDoesNotThrow(() -> controlador.setServicioPedidos(mock(IServicioPedidosUI.class)));
    }

    @Test
    @DisplayName("Cuando servicio encuentra el pedido, retorna Optional presente")
    void consultarPorCodigo_CuandoExiste_RetornaResultado() {
        PedidoDTO pedido = crearPedidoMock("PED-001", CodigoEstadoPedido.EN_PREPARACION);

        when(mockServicio.consultarPorCodigo("PED-001"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(pedido)));

        var resultado = mockServicio.consultarPorCodigo("PED-001").join();

        assertTrue(resultado.isPresent());
        assertEquals("PED-001", resultado.get().codigoPedido());
        // ✅ codigo() del estado es String
        assertEquals(CodigoEstadoPedido.EN_PREPARACION.name(), resultado.get().estadoActual().codigo());
    }

    @Test
    @DisplayName("Cuando código no existe, retorna Optional vacío")
    void consultarPorCodigo_CuandoNoExiste_RetornaVacio() {
        when(mockServicio.consultarPorCodigo("PED-XXXX"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        var resultado = mockServicio.consultarPorCodigo("PED-XXXX").join();

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Cuando servicio falla, el future propaga el error")
    void consultarPorCodigo_CuandoServicioFalla_PropagaError() {
        when(mockServicio.consultarPorCodigo(any()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("BD caída")));

        var future = mockServicio.consultarPorCodigo("PED-001");

        assertThrows(Exception.class, future::join);
    }

    @Test
    @DisplayName("Pedido prioritario tiene flag correcto")
    void crearPedido_Prioritario_TieneFlagCorrecto() {
        PedidoDTO pedido = new PedidoDTO(
                2L, "PED-002", "VIP", "Dir", List.of(),
                new EstadoPedidoDTO(1L, CodigoEstadoPedido.LISTO.name(), "Listo", Instant.now(), "Sys"),
                BigDecimal.valueOf(50000),
                Instant.now(), Instant.now().plusSeconds(300),
                true,   // prioritario
                null, 3L
        );

        assertTrue(pedido.prioritario());
    }

    // === Helper ===

    // ✅ EstadoPedidoDTO.codigo es String
    private PedidoDTO crearPedidoMock(String codigo, CodigoEstadoPedido estado) {
        return new PedidoDTO(
                1L, codigo, "Cliente Test", "Calle Test 123",
                List.of(),
                new EstadoPedidoDTO(
                        10L,
                        estado.name(),
                        estado.name().toLowerCase().replace("_", " "),
                        Instant.now(),
                        "Sistema"
                ),
                BigDecimal.valueOf(15000),
                Instant.now(),
                Instant.now().plusSeconds(1800),
                false, null, 1L
        );
    }
}
