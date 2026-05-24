package com.restaurante.pedidos.servicio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FachadaServiciosPedidoTest {
	@Test
	void exponeServiciosDePedidoCocinaEntregaYSimulacion() {
		FachadaServiciosPedido fachada = new FachadaServiciosPedido();
		assertNotNull(fachada.pedido());
		assertNotNull(fachada.cocina());
		assertNotNull(fachada.entrega());
		assertNotNull(fachada.simulacion());
	}
}
