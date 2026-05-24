package com.restaurante.pedidos.persistencia.controlador;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.EstadoPedido;
import com.restaurante.pedidos.dominio.entidad.Rol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ControladoresJpaTest {
	@Test
	void buscanPorCodigoConControladores() {
		var emf = ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades();
		var roles = new RolesJpaController(emf);
		var estados = new EstadosPedidoJpaController(emf);

		Rol rol = roles.findByCodigo(CodigoRol.ADMINISTRADOR);
		if (rol == null) {
			Rol nuevo = new Rol();
			nuevo.setCodigoRol(CodigoRol.ADMINISTRADOR);
			rol = roles.create(nuevo);
		}

		EstadoPedido estado = estados.findByCodigo(CodigoEstadoPedido.PENDIENTE).orElseGet(() -> {
			EstadoPedido e = new EstadoPedido();
			e.setCodigoEstado(CodigoEstadoPedido.PENDIENTE);
			e.setNombreEstado("Pendiente");
			e.setOrdenEstado(1);
			e.setFinalizado(false);
			return estados.create(e);
		});

		assertNotNull(rol.getId());
		assertNotNull(estado.getId());
		ConfiguracionBaseDatos.cerrar();
	}
}
