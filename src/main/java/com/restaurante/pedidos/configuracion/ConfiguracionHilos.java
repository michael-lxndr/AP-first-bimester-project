package com.restaurante.pedidos.configuracion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class ConfiguracionHilos {
	private static final int KITCHEN_THREADS = 3;
	private static final int DELIVERY_THREADS = 3;
	private static final int SIMULATION_THREADS = 2;

	private static final ExecutorService kitchenExecutor = Executors.newFixedThreadPool(KITCHEN_THREADS);
	private static final ExecutorService deliveryExecutor = Executors.newFixedThreadPool(DELIVERY_THREADS);
	private static final ExecutorService simulationExecutor = Executors.newFixedThreadPool(SIMULATION_THREADS);

	private ConfiguracionHilos() {
	}

	public static ExecutorService kitchenExecutor() {
		return kitchenExecutor;
	}

	public static ExecutorService deliveryExecutor() {
		return deliveryExecutor;
	}

	public static ExecutorService simulationExecutor() {
		return simulationExecutor;
	}

	public static void shutdown() {
		shutdown(kitchenExecutor);
		shutdown(deliveryExecutor);
		shutdown(simulationExecutor);
	}

	private static void shutdown(ExecutorService executorService) {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			executorService.shutdownNow();
		}
	}
}
