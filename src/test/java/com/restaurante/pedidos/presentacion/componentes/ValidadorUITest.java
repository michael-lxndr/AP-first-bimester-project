package com.restaurante.pedidos.presentacion.componentes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidadorUI - Pruebas de validaciones de entrada")
class ValidadorUITest {

    @Test
    @DisplayName("validarCodigoPedido - Código vacío retorna error")
    void validarCodigoPedido_CodigoVacio_RetornaError() {
        Optional<String> error = ValidadorUI.validarCodigoPedido("");
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("obligatorio"));
    }

    @Test
    @DisplayName("validarCodigoPedido - Formato inválido retorna error descriptivo")
    void validarCodigoPedido_FormatInvalido_RetornaErrorDescriptivo() {
        Optional<String> error = ValidadorUI.validarCodigoPedido("pedido#123");
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Formato"), "Mensaje debe mencionar formato: " + error.get());
    }

    @Test
    @DisplayName("validarCodigoPedido - Código válido retorna Optional.empty")
    void validarCodigoPedido_CodigoValido_RetornaVacio() {
        Optional<String> error = ValidadorUI.validarCodigoPedido("PED-ABC123");
        assertFalse(error.isPresent(), "No debe haber error para código válido");
    }

    @Test
    @DisplayName("validarCantidad - Fuera de rango retorna mensaje con límites")
    void validarCantidad_FueraDeRango_RetornaMensajeConLimites() {
        Optional<String> error = ValidadorUI.validarCantidad(0, 1, 10);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Mínimo: 1"));

        error = ValidadorUI.validarCantidad(15, 1, 10);
        assertTrue(error.isPresent());
        assertTrue(error.get().contains("Máximo: 10"));
    }
}