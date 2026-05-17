package com.restaurante.pedidos.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class OrderCodeGenerator {
	private static final SecureRandom random = new SecureRandom();
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

	private OrderCodeGenerator() {
	}

	public static String generate() {
		return "PED-" + LocalDateTime.now().format(formatter) + "-" + randomSuffix();
	}

	private static String randomSuffix() {
		StringBuilder suffix = new StringBuilder(4);
		for (int index = 0; index < 4; index++) {
			suffix.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}

		return suffix.toString();
	}
}
