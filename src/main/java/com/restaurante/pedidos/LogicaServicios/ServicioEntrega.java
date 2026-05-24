/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.Clases.EstadosPedido;
import com.restaurante.pedidos.Clases.PedidosCliente;
import com.restaurante.pedidos.Logica.EstadosPedidoJpaController;
import com.restaurante.pedidos.Logica.PedidosClienteJpaController;
import com.restaurante.pedidos.Logica.exceptions.NonexistentEntityException;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;


public class ServicioEntrega {

	private final MaquinaEstadosPedido maquina;
	private final PedidosClienteJpaController pedidosClienteControlador;
	private final EstadosPedidoJpaController estadosPedidoControlador;

	public ServicioEntrega() {

		maquina = new MaquinaEstadosPedido();

		pedidosClienteControlador = new PedidosClienteJpaController(JPABaseDeDatos.getEntityManagerFactory());
		estadosPedidoControlador = new EstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());
	}

	public void iniciarEntrega(PedidosCliente pedido) throws Exception {

		EstadosPedido estadoEnCamino = estadosPedidoControlador.findEstadosPedido(4L);

		maquina.cambiarEstado(pedido, estadoEnCamino);

		pedidosClienteControlador.edit(pedido);
	}

	public void confirmarEntrega(PedidosCliente pedido) throws Exception {

		EstadosPedido estadoEntregado = estadosPedidoControlador.findEstadosPedido(5L);
		maquina.cambiarEstado(pedido, estadoEntregado);

		pedidosClienteControlador.edit(pedido);
	}
}
