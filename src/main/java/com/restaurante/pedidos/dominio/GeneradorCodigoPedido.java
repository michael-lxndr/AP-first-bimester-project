package com.restaurante.pedidos.dominio;

/**
 * Delegado a nivel de dominio para la generación de códigos únicos de pedidos.
 * Proporciona una interfaz limpia y desacoplada de la capa lógica original.
 */
public class GeneradorCodigoPedido {

    public static String generar() {
        return com.restaurante.pedidos.Logica.Utilidades.GeneradorCodigoPedido.generar();
    }
}
