package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.configuracion.ConfiguracionSimulacion;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public final class SimuladorTiempo {
	private SimuladorTiempo() {
	}

	public static Duration kitchenDelay() {
		return randomBetween(ConfiguracionSimulacion.minKitchenDelay(), ConfiguracionSimulacion.maxKitchenDelay());
	}

	public static Duration deliveryDelay() {
		return randomBetween(ConfiguracionSimulacion.minEntregaDelay(), ConfiguracionSimulacion.maxEntregaDelay());
	}

	static Duration randomBetween(Duration minDelay, Duration maxDelay) {
		long minMillis = minDelay.toMillis();
		long maxMillis = maxDelay.toMillis();
		if (minMillis > maxMillis) {
			throw new IllegalArgumentException("Minimum delay cannot be greater than maximum delay.");
		}

		return Duration.ofMillis(ThreadLocalRandom.current().nextLong(minMillis, maxMillis + 1));
	}
}
