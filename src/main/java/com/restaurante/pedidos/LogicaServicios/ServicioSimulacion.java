/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.Clases.EstadosPedido;
import com.restaurante.pedidos.Clases.PedidosCliente;
import com.restaurante.pedidos.LogicaConfiguracion.ConfiguracionHilos;

import java.util.ArrayList;
import java.util.List;


public class ServicioSimulacion {

	private final ServicioPedido servicioPedido;

	public ServicioSimulacion(ServicioPedido servicioPedido) {
		this.servicioPedido = servicioPedido;
	}

	public List<String> simularPedido(PedidosCliente pedido) {

		List<String> estados = new ArrayList<>();

		ConfiguracionHilos.simulationExecutor().submit(() -> {

			try {

				String codigo = pedido.getCodigoPedido();

				cambiarEstado(codigo, "EN_PREPARACION");
				estados.add("EN_PREPARACION");
				Thread.sleep(2000);

				cambiarEstado(codigo, "LISTO");
				estados.add("LISTO");
				Thread.sleep(2000);

				cambiarEstado(codigo, "EN_CAMINO");
				estados.add("EN_CAMINO");
				Thread.sleep(2000);

				cambiarEstado(codigo, "ENTREGADO");
				estados.add("ENTREGADO");

			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return estados;
	}

	private void cambiarEstado(String codigoPedido, String estado) {

		try {

			PedidosCliente pedido =
				servicioPedido.buscarPedidoPorCodigo(codigoPedido);

			if (pedido == null) {
				return;
			}

			EstadosPedido nuevoEstado =
				servicioPedido.buscarEstadoPorCodigo(estado);

			pedido.setEstadoActualId(nuevoEstado);

			servicioPedido.actualizar(pedido);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
