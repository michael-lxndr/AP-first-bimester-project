package com.restaurante.pedidos.negocio.servicios;

import com.restaurante.pedidos.LogicaServicios.ServicioSimulacionConcurrente;

/**
 * Servicio de negocio que controla el ciclo de vida del motor de simulación.
 */
public class SimulacionService {
    private final ServicioSimulacionConcurrente simulacion;

    public SimulacionService(ServicioSimulacionConcurrente simulacion) {
        this.simulacion = simulacion;
    }

    public void iniciarSimulacion(int intervaloSeconds, int numCooks) {
        simulacion.startSimulation(intervaloSeconds, numCooks);
    }

    public void detenerCocina() {
        simulacion.stopKitchen();
    }

    public void detenerInmediatamente() {
        simulacion.shutdownImmediately();
    }

    public boolean estaSimulando() {
        return simulacion.isSimulating();
    }
}
