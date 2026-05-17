package com.restaurante.pedidos.config;

import java.time.Duration;

public final class SimulationConfig {
	private static final Duration MIN_KITCHEN_DELAY = Duration.ofSeconds(3);
	private static final Duration MAX_KITCHEN_DELAY = Duration.ofSeconds(8);
	private static final Duration MIN_DELIVERY_DELAY = Duration.ofSeconds(5);
	private static final Duration MAX_DELIVERY_DELAY = Duration.ofSeconds(12);

	private SimulationConfig() {
	}

	public static Duration minKitchenDelay() {
		return MIN_KITCHEN_DELAY;
	}

	public static Duration maxKitchenDelay() {
		return MAX_KITCHEN_DELAY;
	}

	public static Duration minDeliveryDelay() {
		return MIN_DELIVERY_DELAY;
	}

	public static Duration maxDeliveryDelay() {
		return MAX_DELIVERY_DELAY;
	}
}
