/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaConfiguracion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Javier Montaño
 */
public class ConfiguracionHilos {

    private static final int KITCHEN_THREADS = 3;
    private static final int DELIVERY_THREADS = 3;
    private static final int SIMULATION_THREADS = 2;

    private static final ExecutorService kitchenExecutor = Executors.newFixedThreadPool(KITCHEN_THREADS);

    private static final ExecutorService deliveryExecutor = Executors.newFixedThreadPool(DELIVERY_THREADS);

    private static final ExecutorService simulationExecutor = Executors.newFixedThreadPool(SIMULATION_THREADS);

    private ConfiguracionHilos() {
    }

    // GETTERS DE POOLS
    public static ExecutorService kitchenExecutor() {
        return kitchenExecutor;
    }

    public static ExecutorService deliveryExecutor() {
        return deliveryExecutor;
    }

    public static ExecutorService simulationExecutor() {
        return simulationExecutor;
    }

    // SHUTDOWN GLOBAL

    public static void shutdown() {
        shutdownExecutor(kitchenExecutor);
        shutdownExecutor(deliveryExecutor);
        shutdownExecutor(simulationExecutor);
    }

    private static void shutdownExecutor(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }
}
