/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.restaurante.pedidos.presentacion;

import com.restaurante.pedidos.clases.Clientes;
import com.restaurante.pedidos.clases.DireccionesCliente;
import com.restaurante.pedidos.clases.PedidosCliente;
import com.restaurante.pedidos.clases.Personal;
import com.restaurante.pedidos.LogicaServicios.ServicioPedido;
import com.restaurante.pedidos.LogicaServicios.ServicioPersonal;
import com.restaurante.pedidos.LogicaServicios.ServicioSimulacion;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class Main {

	private Scanner scanner;
	private ServicioPedido servicioPedido;
	private ServicioSimulacion servicioSimulacion;
	private ServicioPersonal servicioPersonal;

	public static void main(String[] args) {

		Main app = new Main();

		app.scanner = new Scanner(System.in);
		app.servicioPedido = new ServicioPedido();
		app.servicioSimulacion = new ServicioSimulacion(app.servicioPedido);
		app.servicioPersonal = new ServicioPersonal();

		app.iniciarSistema();
	}

	public void iniciarSistema() {
		int opcion;

		do {
			mostrarMenu();

			opcion = Integer.parseInt(scanner.nextLine());

			switch (opcion) {
				case 1:
					crearPedido();
					break;

				case 2:
					simularPedido();
					break;

				case 3:
					consultarEstadoPedido();
					break;

				case 4:
					gestionarPersonal();
					break;

				case 0:

					System.out.println("\nSaliendo del sistema...");
					break;

				default:

					System.out.println("\nOpcion no válida");
			}
		} while (opcion != 0);
	}

	private void mostrarMenu() {

		System.out.println("RESTAURANT DELIVERY SYSTEM");

		System.out.println("1. Crear Pedido");
		System.out.println("2. Simular Pedido");
		System.out.println("3. Consultar Estado Pedido");
		System.out.println("4. Gestionar Personal");
		System.out.println("0. Salir");
		System.out.println("\nSeleccione una opción: ");
	}

	private void gestionarPersonal() {
		mostrarMenuPersonal();

		int op = Integer.parseInt(scanner.nextLine());

		servicioPersonal.ejecutarOpcion(op);
	}

	private void mostrarMenuPersonal() {
		System.out.println("\nGESTION PERSONAL");
		System.out.println("1. Ver Personal");
		System.out.println("2. Asignar Repartidor");
		System.out.println("0. Volver");
		System.out.println("Seleccione una opción: ");
	}

	private void crearPedido() {

		try {
			System.out.println(" Creación de un pedido ");

			System.out.print("Código de pedido: ");
			String codigo = scanner.nextLine();

			System.out.print("Dirección de entrega: ");
			String direccionTexto = scanner.nextLine();

			System.out.print("Total de pedido: ");
			BigDecimal total = new BigDecimal(scanner.nextLine());

			// 1. Clientes, creamos las entidades
			Clientes cliente = new Clientes();
			cliente.setClienteId(1L);

			// 2. Direcciones
			DireccionesCliente direccion = new DireccionesCliente();
			direccion.setDireccionId(1L);

			// 3. Peronal encargado
			Personal personal = new Personal();
			personal.setPersonalId(1L);

			// 4. Pedidos a crear
			PedidosCliente pedido = new PedidosCliente();
			pedido.setCodigoPedido(codigo);

			pedido.setSnapshotDireccionEntrega(direccionTexto);

			pedido.setTotal(total);

			pedido.setPrioritario(false);

			pedido.setCreadoEn(new Date());

			pedido.setClienteId(cliente);

			pedido.setDireccionEntregaId(direccion);

			pedido.setRegistradoPorPersonalId(personal);

			// Lo guardamos al pedido

			servicioPedido.crearPedido(pedido);

			System.out.println("\nPedido creado correctamente");
			System.out.println("Estado incial: PENDIENTE");

		} catch (Exception e) {
			System.out.println("Error creando pedido");
			e.printStackTrace();
		}
	}

	private void simularPedido() {

		try {
			System.out.println("\nSimular Pedido");

			System.out.print("Ingrese el código del pedido: ");
			String codigo = scanner.nextLine();

			PedidosCliente pedido = servicioPedido.buscarPedidoPorCodigo(codigo);

			if (pedido == null) {
				System.out.println("\nEl pedido no fue encontrado");

				return;
			}

			System.out.println("Estado incial: " + pedido.getEstadoActualId().getCodigoEstado());

			List<String> estados = servicioSimulacion.simularPedido(pedido);
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (String estado : estados) {
				System.out.println("Estado actualizado a: " + estado);
			}

			pedido = servicioPedido.buscarPedidoPorCodigo(codigo);

			System.out.println("Estado final: " + pedido.getEstadoActualId().getCodigoEstado());
		} catch (Exception e) {
			System.out.println("Error en simulacion");
			e.printStackTrace();
		}
	}

	private void consultarEstadoPedido() {
		System.out.println("\nConsultar el Estado");

		System.out.print("Ingrese el código del pedido");
		String codigo = scanner.nextLine();

		String estado = servicioPedido.consultarEstadoPorCodigo(codigo);

		System.out.println("\nEstado actual: " + estado);
	}
}
