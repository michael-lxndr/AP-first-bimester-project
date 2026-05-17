package com.restaurante.pedidos.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneradorCodigoPedidoTest {
	@Test
	void generarDevuelveFormatoEsperadoParaCodigoDePedido() {
		String code = GeneradorCodigoPedido.generar();

		assertTrue(code.matches("PED-\\d{14}-[A-Z2-9]{4}"));
	}
}
