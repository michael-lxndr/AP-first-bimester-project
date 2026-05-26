package com.restaurante.pedidos.presentacion;

import javafx.application.Application;

/**
 * Entrada principal no-Application que envuelve el lanzamiento de JavaFX.
 * Esto evita problemas del classpath de JavaFX en entornos sin modularidad activa.
 */
public class AplicacionPrincipal {

    public static void main(String[] args) {
        Application.launch(AplicacionJavaFx.class, args);
    }
}
