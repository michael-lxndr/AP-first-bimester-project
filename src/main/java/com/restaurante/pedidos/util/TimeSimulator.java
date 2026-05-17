package com.restaurante.pedidos.util;

import com.restaurante.pedidos.config.SimulationConfig;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public final class TimeSimulator {
	private TimeSimulator() {
	}

	public static Duration kitchenDelay() {
		return randomBetween(SimulationConfig.minKitchenDelay(), SimulationConfig.maxKitchenDelay());
	}

	public static Duration deliveryDelay() {
		return randomBetween(SimulationConfig.minDeliveryDelay(), SimulationConfig.maxDeliveryDelay());
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
