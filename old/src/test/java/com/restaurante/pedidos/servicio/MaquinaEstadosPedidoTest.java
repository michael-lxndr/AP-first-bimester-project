package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.EstadoPedido;
import com.restaurante.pedidos.dominio.entidad.ReglaTransicionEstadoPedido;
import com.restaurante.pedidos.dominio.entidad.Rol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaquinaEstadosPedidoTest {
	@Test
	void validaTransicionConReglaActiva() {
		MaquinaEstadosPedido maquina = new MaquinaEstadosPedido();
		assertTrue(maquina.esTransicionValida(CodigoEstadoPedido.PENDIENTE, CodigoEstadoPedido.EN_PREPARACION, CodigoRol.COCINERO, List.of(regla(CodigoRol.COCINERO, CodigoEstadoPedido.PENDIENTE, CodigoEstadoPedido.EN_PREPARACION, true))));
	}

	@Test
	void rechazaTransicionSinReglaActiva() {
		MaquinaEstadosPedido maquina = new MaquinaEstadosPedido();
		assertFalse(maquina.esTransicionValida(CodigoEstadoPedido.PENDIENTE, CodigoEstadoPedido.EN_CAMINO, CodigoRol.REPARTIDOR, List.of(regla(CodigoRol.REPARTIDOR, CodigoEstadoPedido.LISTO, CodigoEstadoPedido.EN_CAMINO, true))));
	}

	private ReglaTransicionEstadoPedido regla(CodigoRol rolCodigo, CodigoEstadoPedido origenCodigo, CodigoEstadoPedido destinoCodigo, boolean activa) {
		Rol rol = new Rol();
		rol.setCodigoRol(rolCodigo);
		EstadoPedido origen = new EstadoPedido();
		origen.setCodigoEstado(origenCodigo);
		EstadoPedido destino = new EstadoPedido();
		destino.setCodigoEstado(destinoCodigo);
		ReglaTransicionEstadoPedido regla = new ReglaTransicionEstadoPedido();
		regla.setRol(rol);
		regla.setEstadoOrigen(origen);
		regla.setEstadoDestino(destino);
		regla.setActiva(activa);
		return regla;
	}
}
