/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.Clases.Enums.CodigoEstadoPedido;
import com.restaurante.pedidos.Clases.EstadosPedido;
import com.restaurante.pedidos.Clases.PedidosCliente;


public class MaquinaEstadosPedido {

	public boolean validarCambioEstado(CodigoEstadoPedido actual, CodigoEstadoPedido nuevo) {

		switch (actual) {

			case PENDIENTE:
				return nuevo == CodigoEstadoPedido.EN_PREPARACION;

			case EN_PREPARACION:
				return nuevo == CodigoEstadoPedido.LISTO;

			case LISTO:
				return nuevo == CodigoEstadoPedido.EN_CAMINO;

			case EN_CAMINO:
				return nuevo == CodigoEstadoPedido.ENTREGADO;

			default:
				return false;
		}
	}

	public void cambiarEstado(PedidosCliente pedido, EstadosPedido nuevoEstadoEntidad) {

		CodigoEstadoPedido actual = CodigoEstadoPedido.valueOf(pedido.getEstadoActualId().getCodigoEstado());

		CodigoEstadoPedido nuevo = CodigoEstadoPedido.valueOf(nuevoEstadoEntidad.getCodigoEstado());

		boolean valido = validarCambioEstado(actual, nuevo);

		if (!valido) {
			throw new IllegalStateException("Transaccion de estado inválida");
		}

		pedido.setEstadoActualId(nuevoEstadoEntidad);
	}

	public boolean esTransicionValida(
			com.restaurante.pedidos.dominio.CodigoEstadoPedido origen,
			com.restaurante.pedidos.dominio.CodigoEstadoPedido destino,
			com.restaurante.pedidos.dominio.CodigoRol rol,
			java.util.List<com.restaurante.pedidos.Clases.ReglasTransicionEstadoPedido> reglas) {
		if (reglas == null) {
			return false;
		}
		for (com.restaurante.pedidos.Clases.ReglasTransicionEstadoPedido regla : reglas) {
			if (regla.getActiva() &&
				regla.getRolId() != null && rol.name().equals(regla.getRolId().getCodigoRol()) &&
				regla.getEstadoOrigenId() != null && origen.name().equals(regla.getEstadoOrigenId().getCodigoEstado()) &&
				regla.getEstadoDestinoId() != null && destino.name().equals(regla.getEstadoDestinoId().getCodigoEstado())) {
				return true;
			}
		}
		return false;
	}
}
