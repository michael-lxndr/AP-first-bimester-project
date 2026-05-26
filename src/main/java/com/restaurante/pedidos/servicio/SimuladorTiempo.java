package com.restaurante.pedidos.servicio;

import java.time.Duration;
import com.restaurante.pedidos.logica.Utilidades.SimularTiempo;

/**
 * Clase puente de compatibilidad para la simulación de demoras en la suite de pruebas.
 * Delega en la clase de producción SimularTiempo.
 */
public class SimuladorTiempo {

    public static Duration randomBetween(Duration minDelay, Duration maxDelay) {
        return SimularTiempo.randomBetween(minDelay, maxDelay);
    }
}
