package com.restaurante.pedidos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderCodeGeneratorTest {
	@Test
	void generateReturnsExpectedRestaurantOrderFormat() {
		String code = OrderCodeGenerator.generate();

		assertTrue(code.matches("PED-\\d{14}-[A-Z2-9]{4}"));
	}
}
