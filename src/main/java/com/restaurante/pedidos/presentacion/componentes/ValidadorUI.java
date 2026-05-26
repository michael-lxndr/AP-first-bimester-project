package com.restaurante.pedidos.presentacion.componentes;

import java.util.Optional;
import java.util.regex.Pattern;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Validaciones de entrada para UI.
 *
 * Regla de oro: Esto NO es lógica de negocio.
 * Solo valida formato, no reglas como "un pedido no puede tener 0 items".
 */
public final class ValidadorUI {

    private static final Pattern PATRON_CODIGO_PEDIDO = Pattern.compile("^[A-Z0-9-]{6,30}$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("^\\d{7,10}$");

    private ValidadorUI() {}

    // === Validadores que retornan mensaje de error (para mostrar en UI) ===

    public static Optional<String> validarCodigoPedido(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.of("El código es obligatorio");
        }
        if (!PATRON_CODIGO_PEDIDO.matcher(codigo.trim().toUpperCase()).matches()) {
            return Optional.of("Formato: 6-30 caracteres, solo letras mayúsculas, números y guiones");
        }
        return Optional.empty();
    }

    public static Optional<String> validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return Optional.of("El teléfono es obligatorio");
        }
        String limpio = telefono.replaceAll("[\\s\\-\\.\\(\\)]", "");
        if (!PATRON_TELEFONO.matcher(limpio).matches()) {
            return Optional.of("Ingrese 7-10 dígitos numéricos");
        }
        return Optional.empty();
    }

    public static Optional<String> validarCantidad(int cantidad, int min, int max) {
        if (cantidad < min) {
            return Optional.of("Mínimo: " + min);
        }
        if (cantidad > max) {
            return Optional.of("Máximo: " + max);
        }
        return Optional.empty();
    }

    // === Formateadores para TextField (bindings reactivos) ===

    /**
     * Aplica máscara de solo números a un TextField.
     * Uso: campo.setTextFormatter(ValidadorUI.formateadorSoloNumeros());
     */
    public static TextFormatter<String> formateadorSoloNumeros() {
        return new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d*")) {
                return change;
            }
            return null; // Rechaza el cambio
        });
    }

    /**
     * Convierte texto a mayúsculas en tiempo real.
     */
    public static TextFormatter<String> formateadorMayusculas() {
        return new TextFormatter<>(change -> {
            change.setText(change.getText().toUpperCase());
            return change;
        });
    }

    /**
     * Limita longitud máxima de un TextField.
     */
    public static TextFormatter<String> formateadorLongitudMaxima(int max) {
        return new TextFormatter<>(change ->
                change.getControlNewText().length() <= max ? change : null
        );
    }
}