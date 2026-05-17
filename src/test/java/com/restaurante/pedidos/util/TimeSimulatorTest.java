package com.restaurante.pedidos.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSimulatorTest {
	@Test
	void randomBetweenReturnsDelayInsideRange() {
		Duration minDelay = Duration.ofMillis(10);
		Duration maxDelay = Duration.ofMillis(20);

		Duration delay = TimeSimulator.randomBetween(minDelay, maxDelay);

		assertTrue(delay.compareTo(minDelay) >= 0);
		assertTrue(delay.compareTo(maxDelay) <= 0);
	}

	@Test
	void randomBetweenAllowsSameMinimumAndMaximumDelay() {
		Duration delay = TimeSimulator.randomBetween(Duration.ofMillis(15), Duration.ofMillis(15));

		assertEquals(Duration.ofMillis(15), delay);
	}

	@Test
	void randomBetweenRejectsInvalidRange() {
		assertThrows(
			IllegalArgumentException.class,
			() -> TimeSimulator.randomBetween(Duration.ofMillis(20), Duration.ofMillis(10))
		);
	}
}
