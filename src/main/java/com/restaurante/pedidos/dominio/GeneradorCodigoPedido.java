package com.restaurante.pedidos.dominio;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GeneradorCodigoPedido {
	private static final SecureRandom random = new SecureRandom();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

	private GeneradorCodigoPedido() {
	}

	public static String generar() {
		return "PED-" + LocalDateTime.now().format(formatter) + "-" + sufijoAleatorio();
	}

	private static String sufijoAleatorio() {
		StringBuilder sufijo = new StringBuilder(4);
		for (int indice = 0; indice < 4; indice++) {
			sufijo.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}

		return sufijo.toString();
	}
}
