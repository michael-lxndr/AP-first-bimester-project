package com.restaurante.pedidos.servicio;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimuladorTiempoTest {
	@Test
	void randomBetweenReturnsDelayInsideRange() {
		Duration minDelay = Duration.ofMillis(10);
		Duration maxDelay = Duration.ofMillis(20);

		Duration delay = SimuladorTiempo.randomBetween(minDelay, maxDelay);

		assertTrue(delay.compareTo(minDelay) >= 0);
		assertTrue(delay.compareTo(maxDelay) <= 0);
	}

	@Test
	void randomBetweenAllowsSameMinimumAndMaximumDelay() {
		Duration delay = SimuladorTiempo.randomBetween(Duration.ofMillis(15), Duration.ofMillis(15));

		assertEquals(Duration.ofMillis(15), delay);
	}

	@Test
	void randomBetweenRejectsInvalidRange() {
		assertThrows(
			IllegalArgumentException.class,
			() -> SimuladorTiempo.randomBetween(Duration.ofMillis(20), Duration.ofMillis(10))
		);
	}
}
