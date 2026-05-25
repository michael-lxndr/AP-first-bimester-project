package com.restaurante.pedidos.LogicaConfiguracion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConfiguracionHilos {

    private static final int KITCHEN_THREADS = 3;
    private static final int DELIVERY_THREADS = 3;
    private static final int SIMULATION_THREADS = 2;

    private static final ExecutorService kitchenExecutor = Executors.newFixedThreadPool(KITCHEN_THREADS);
    private static final ExecutorService deliveryExecutor = Executors.newFixedThreadPool(DELIVERY_THREADS);
    private static final ExecutorService simulationExecutor = Executors.newFixedThreadPool(SIMULATION_THREADS);

    private ConfiguracionHilos() {}

    public static ExecutorService kitchenExecutor() { return kitchenExecutor; }
    public static ExecutorService deliveryExecutor() { return deliveryExecutor; }
    public static ExecutorService simulationExecutor() { return simulationExecutor; }

    // Métodos para obtener el tamaño del pool
    public static int getKitchenThreads() { return KITCHEN_THREADS; }
    public static int getDeliveryThreads() { return DELIVERY_THREADS; }
    public static int getSimulationThreads() { return SIMULATION_THREADS; }

    public static void shutdown() {
        shutdownExecutor(kitchenExecutor, "Cocina");
        shutdownExecutor(deliveryExecutor, "Delivery");
        shutdownExecutor(simulationExecutor, "Simulación");
    }

    private static void shutdownExecutor(ExecutorService executor, String nombre) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("⚠️ " + nombre + " no terminó a tiempo, forzando cierre...");
                executor.shutdownNow();
            } else {
                System.out.println("✅ " + nombre + " cerrado correctamente");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}