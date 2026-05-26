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


 public class ServicioCocina {

	 private final MaquinaEstadosPedido maquina;
	 private final PedidosClienteJpaController pedidosClienteControlador;
	 private final EstadosPedidoJpaController estadosPedidoControlador;

	 public ServicioCocina() {

		 maquina = new MaquinaEstadosPedido();
		 pedidosClienteControlador = new PedidosClienteJpaController(JPABaseDeDatos.getEntityManagerFactory());

		 estadosPedidoControlador = new EstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());

	 }

	 public void iniciarPreparacion(PedidosCliente pedido) throws Exception {

		 EstadosPedido estadoPreparacion = estadosPedidoControlador.findEstadosPedido(2L);

		 maquina.cambiarEstado(pedido, estadoPreparacion);

		 pedidosClienteControlador.edit(pedido);
	 }

	 public void marcarListo(PedidosCliente pedido) throws Exception {

		 EstadosPedido estadoListo = estadosPedidoControlador.findEstadosPedido(3L);

		 maquina.cambiarEstado(pedido, estadoListo);

		 pedidosClienteControlador.edit(pedido);
	 }
 }
