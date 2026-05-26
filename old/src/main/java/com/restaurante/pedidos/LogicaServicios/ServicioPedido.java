/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.clases.EstadosPedido;
import com.restaurante.pedidos.clases.PedidosCliente;
import com.restaurante.pedidos.logica.EstadosPedidoJpaController;
import com.restaurante.pedidos.logica.PedidosClienteJpaController;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;


public class ServicioPedido {

	private final PedidosClienteJpaController pedidosClienteControlador;
	private final EstadosPedidoJpaController estadosPedidoControlador;

	public ServicioPedido() {
		pedidosClienteControlador =
			new PedidosClienteJpaController(JPABaseDeDatos.getEntityManagerFactory());

		estadosPedidoControlador =
			new EstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());
	}

	public void crearPedido(PedidosCliente nuevoPedido) throws Exception {

		EstadosPedido estadoInicial = estadosPedidoControlador.findEstadosPedido(1L);

		nuevoPedido.setEstadoActualId(estadoInicial);

		pedidosClienteControlador.create(nuevoPedido);
	}

	public void actualizar(PedidosCliente pedido) throws Exception {
		pedidosClienteControlador.edit(pedido);
	}

	public PedidosCliente buscarPedidoPorCodigo(String codigo) {
		return pedidosClienteControlador.findByCodigo(codigo);
	}

	public String consultarEstadoPorCodigo(String codigo) {

		PedidosCliente pedido = pedidosClienteControlador.findByCodigo(codigo);

		if (pedido == null) {
			return "Pedido no encontrado";
		}

		return pedido.getEstadoActualId().getCodigoEstado();
	}

	public EstadosPedido buscarEstadoPorCodigo(String codigoEstado) {

		for (EstadosPedido estado : estadosPedidoControlador.findEstadosPedidoEntities()) {

			if (estado.getCodigoEstado().equals(codigoEstado)) {
				return estado;
			}
		}

		return null;
	}
}
