package com.restaurante.pedidos.presentacion;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AplicacionConsolaTest {
	@Test
	void menuEsRepetibleYRecuperaEntradaInvalida() {
		String entradas = "x\n1\n0\n";
		ByteArrayInputStream input = new ByteArrayInputStream(entradas.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(outputBytes);
		AtomicInteger ejecuciones = new AtomicInteger();

		AplicacionConsola.ejecutar(input, output, ejecuciones::incrementAndGet);

		assertEquals(1, ejecuciones.get());
		String salida = outputBytes.toString(StandardCharsets.UTF_8);
		assertTrue(salida.contains("Opción inválida"));
		assertTrue(salida.contains("Seleccione una opción"));
	}
}
