/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.logica.Utilidades;

import com.restaurante.pedidos.LogicaConfiguracion.ConfiguracionSimulacion;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;


public class SimularTiempo {

	private SimularTiempo() {
	}

	public static Duration kitchenDelay() {
		return randomBetween(ConfiguracionSimulacion.minKitchenDelay(), ConfiguracionSimulacion.maxKitchenDelay());
	}

	public static Duration deliveryDelay() {
		return randomBetween(ConfiguracionSimulacion.minEntregaDelay(), ConfiguracionSimulacion.maxEntregaDelay());
	}

	public static void esperarPreparacion() throws InterruptedException {
		Thread.sleep(kitchenDelay().toMillis());
	}

	public static void esperarEntrega() throws InterruptedException {
		Thread.sleep(deliveryDelay().toMillis());
	}

	public static Duration randomBetween(Duration minDelay, Duration maxDelay) {
		long minMillis = minDelay.toMillis();
		long maxMillis = maxDelay.toMillis();
		if (minMillis > maxMillis) {
			throw new IllegalArgumentException("Minimum delay cannot be greater than maximum delay.");
		}

		return Duration.ofMillis(ThreadLocalRandom.current().nextLong(minMillis, maxMillis + 1));
	}
}
