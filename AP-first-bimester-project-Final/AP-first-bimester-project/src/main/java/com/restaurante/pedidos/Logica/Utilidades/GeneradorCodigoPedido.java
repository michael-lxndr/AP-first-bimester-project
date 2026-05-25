/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.Logica.Utilidades;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class GeneradorCodigoPedido {
	private static final SecureRandom random =
		new SecureRandom();

	private static final DateTimeFormatter formatter =
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private static final String ALPHABET =
		"ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

	private GeneradorCodigoPedido() {
	}

	public static String generar() {

		return "PED-"
			+ LocalDateTime.now().format(formatter)
			+ "-"
			+ sufijoAleatorio();
	}

	private static String sufijoAleatorio() {

		StringBuilder sufijo =
			new StringBuilder(4);

		for (int indice = 0; indice < 4; indice++) {

			sufijo.append(
				ALPHABET.charAt(
					random.nextInt(
						ALPHABET.length())));
		}

		return sufijo.toString();
	}

}
