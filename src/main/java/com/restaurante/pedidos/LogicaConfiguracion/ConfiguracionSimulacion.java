/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaConfiguracion;

import java.time.Duration;

/**
 *
 * @author Javier Montaño
 */
public class ConfiguracionSimulacion {

    private static final Duration MIN_KITCHEN_DELAY = Duration.ofSeconds(3);
	private static final Duration MAX_KITCHEN_DELAY = Duration.ofSeconds(8);
	private static final Duration MIN_DELIVERY_DELAY = Duration.ofSeconds(5);
	private static final Duration MAX_DELIVERY_DELAY = Duration.ofSeconds(12);

	private ConfiguracionSimulacion() {
	}

	public static Duration minKitchenDelay() {
		return MIN_KITCHEN_DELAY;
	}

	public static Duration maxKitchenDelay() {
		return MAX_KITCHEN_DELAY;
	}

	public static Duration minEntregaDelay() {
		return MIN_DELIVERY_DELAY;
	}

	public static Duration maxEntregaDelay() {
		return MAX_DELIVERY_DELAY;
	}

}
