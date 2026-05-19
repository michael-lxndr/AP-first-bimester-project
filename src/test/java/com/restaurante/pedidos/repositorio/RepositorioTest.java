package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CategoriaProducto;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.GeneradorCodigoPedido;
import com.restaurante.pedidos.dominio.entidad.Cliente;
import com.restaurante.pedidos.dominio.entidad.DireccionCliente;
import com.restaurante.pedidos.dominio.entidad.Entrega;
import com.restaurante.pedidos.dominio.entidad.EstadoPedido;
import com.restaurante.pedidos.dominio.entidad.PedidoCliente;
import com.restaurante.pedidos.dominio.entidad.Personal;
import com.restaurante.pedidos.dominio.entidad.Producto;
import com.restaurante.pedidos.dominio.entidad.ReglaTransicionEstadoPedido;
import com.restaurante.pedidos.dominio.entidad.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RepositorioTest {
	@Test
	void usandoTodosLosRepositorios() {
		EntityTransaction transaccion = null;

		try (EntityManager administradorDeEntidad = ConfiguracionBaseDatos.crearAdministradorDeEntidad()) {
			RepositorioRol repositorioRol = new RepositorioRol(administradorDeEntidad);
			RepositorioEstadoPedido repositorioEstadoPedido = new RepositorioEstadoPedido(administradorDeEntidad);
			RepositorioCliente repositorioCliente = new RepositorioCliente(administradorDeEntidad);
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(administradorDeEntidad);
			RepositorioProducto repositorioProducto = new RepositorioProducto(administradorDeEntidad);
			RepositorioPedido repositorioPedido = new RepositorioPedido(administradorDeEntidad);
			RepositorioReglaTransicionEstadoPedido repositorioReglas =
				new RepositorioReglaTransicionEstadoPedido(administradorDeEntidad);
			RepositorioEntrega repositorioEntrega = new RepositorioEntrega(administradorDeEntidad);

			transaccion = administradorDeEntidad.getTransaction();
			transaccion.begin();

			Instant ahora = Instant.now();
			String sufijo = Long.toString(System.nanoTime());
			String sufijoCorto = sufijo.substring(Math.max(0, sufijo.length() - 6));

			Rol rolAdministrador = obtenerOCrearRol(repositorioRol, CodigoRol.ADMINISTRADOR);
			Rol rolCocinero = obtenerOCrearRol(repositorioRol, CodigoRol.COCINERO);
			Rol rolRepartidor = obtenerOCrearRol(repositorioRol, CodigoRol.REPARTIDOR);

			EstadoPedido estadoPendiente = obtenerOCrearEstado(
				repositorioEstadoPedido,
				CodigoEstadoPedido.PENDIENTE,
				"Pendiente",
				1,
				false
			);
			EstadoPedido estadoEnPreparacion = obtenerOCrearEstado(
				repositorioEstadoPedido,
				CodigoEstadoPedido.EN_PREPARACION,
				"En preparacion",
				2,
				false
			);
			EstadoPedido estadoListo = obtenerOCrearEstado(
				repositorioEstadoPedido,
				CodigoEstadoPedido.LISTO,
				"Listo",
				3,
				false
			);
			EstadoPedido estadoEnCamino = obtenerOCrearEstado(
				repositorioEstadoPedido,
				CodigoEstadoPedido.EN_CAMINO,
				"En camino",
				4,
				false
			);
			EstadoPedido estadoEntregado = obtenerOCrearEstado(
				repositorioEstadoPedido,
				CodigoEstadoPedido.ENTREGADO,
				"Entregado",
				5,
				true
			);

			asegurarRegla(
				repositorioReglas,
				repositorioRol,
				repositorioEstadoPedido,
				CodigoRol.COCINERO,
				CodigoEstadoPedido.PENDIENTE,
				CodigoEstadoPedido.EN_PREPARACION
			);
			asegurarRegla(
				repositorioReglas,
				repositorioRol,
				repositorioEstadoPedido,
				CodigoRol.REPARTIDOR,
				CodigoEstadoPedido.LISTO,
				CodigoEstadoPedido.EN_CAMINO
			);

			Cliente cliente = Cliente.builder()
				.nombreCompleto("Cliente Demo " + sufijo)
				.telefono("099" + sufijo.substring(Math.max(0, sufijo.length() - 7)))
				.correoElectronico("cliente." + sufijo + "@test.com")
				.activo(true)
				.creadoEn(ahora)
				.build();
			repositorioCliente.guardar(cliente);

			DireccionCliente direccionCliente = DireccionCliente.builder()
				.cliente(cliente)
				.alias("Casa demo")
				.callePrincipal("Av. Siempre Viva")
				.calleSecundaria("Calle 1")
				.numeroCasa("123")
				.referencia("Porton gris")
				.codigoPostal("170101")
				.ciudad("Quito")
				.provincia("Pichincha")
				.pais("Ecuador")
				.principal(true)
				.activa(true)
				.build();
			// DireccionCliente forma parte del dominio, pero Persona 1 no definio un repositorio dedicado.
			administradorDeEntidad.persist(direccionCliente);

			Personal administrador = repositorioPersonal.guardar(Personal.builder()
				.rol(rolAdministrador)
				.nombreCompleto("Admin Demo " + sufijo)
				.telefono("0981000001")
				.correoElectronico("admin." + sufijo + "@test.com")
				.nombreUsuario("admin_" + sufijo)
				.activo(true)
				.creadoEn(ahora)
				.build());

			Personal cocinero = repositorioPersonal.guardar(Personal.builder()
				.rol(rolCocinero)
				.nombreCompleto("Cocinero Demo " + sufijo)
				.telefono("0981000002")
				.correoElectronico("cocinero." + sufijo + "@test.com")
				.nombreUsuario("cocinero_" + sufijo)
				.activo(true)
				.creadoEn(ahora.plusSeconds(10))
				.build());

			Personal repartidorDisponible = repositorioPersonal.guardar(Personal.builder()
				.rol(rolRepartidor)
				.nombreCompleto("Repartidor Libre " + sufijo)
				.telefono("0981000003")
				.correoElectronico("repartidor.libre." + sufijo + "@test.com")
				.nombreUsuario("repartidor_libre_" + sufijo)
				.activo(true)
				.creadoEn(ahora.plusSeconds(20))
				.build());

			Personal repartidorOcupado = repositorioPersonal.guardar(Personal.builder()
				.rol(rolRepartidor)
				.nombreCompleto("Repartidor Ocupado " + sufijo)
				.telefono("0981000004")
				.correoElectronico("repartidor.ocupado." + sufijo + "@test.com")
				.nombreUsuario("repartidor_ocupado_" + sufijo)
				.activo(true)
				.creadoEn(ahora.plusSeconds(30))
				.build());

			Producto productoDisponible = repositorioProducto.guardar(Producto.builder()
				.codigoProducto("PROD-" + sufijoCorto)
				.nombreProducto("Pizza Demo " + sufijo)
				.descripcion("Producto disponible para probar repositorios")
				.precioUnitario(new BigDecimal("15.00"))
				.costoProduccion(new BigDecimal("8.00"))
				.categoria(CategoriaProducto.PLATO_FUERTE)
				.tiempoPreparacionMinutos(20)
				.disponible(true)
				.urlImagen("https://example.com/demo-pizza.jpg")
				.creadoEn(ahora)
				.build());

			repositorioProducto.guardar(Producto.builder()
				.codigoProducto("PND-" + sufijoCorto)
				.nombreProducto("Bebida No Disponible " + sufijo)
				.descripcion("Producto no disponible para validar filtros")
				.precioUnitario(new BigDecimal("3.00"))
				.costoProduccion(new BigDecimal("1.00"))
				.categoria(CategoriaProducto.BEBIDA)
				.tiempoPreparacionMinutos(3)
				.disponible(false)
				.urlImagen("https://example.com/demo-bebida.jpg")
				.creadoEn(ahora.plusSeconds(5))
				.build());

			PedidoCliente pedidoPrioritario = repositorioPedido.guardar(crearPedido(
				cliente,
				administrador,
				estadoPendiente,
				direccionCliente,
				"prioritario-" + sufijo,
				true,
				ahora.plusSeconds(60)
			));

			PedidoCliente pedidoNormal = repositorioPedido.guardar(crearPedido(
				cliente,
				administrador,
				estadoPendiente,
				direccionCliente,
				"normal-" + sufijo,
				false,
				ahora.plusSeconds(120)
			));

			PedidoCliente pedidoEnRuta = repositorioPedido.guardar(crearPedido(
				cliente,
				administrador,
				estadoEnCamino,
				direccionCliente,
				"ruta-" + sufijo,
				false,
				ahora.plusSeconds(180)
			));

			Entrega entregaActiva = repositorioEntrega.guardar(Entrega.builder()
				.pedido(pedidoEnRuta)
				.repartidor(repartidorOcupado)
				.despachadoEn(ahora.plusSeconds(240))
				.entregadoEn(null)
				.nombreReceptor(null)
				.confirmadoPorClienteEn(null)
				.notasConfirmacionCliente(null)
				.estadoEntrega("DESPACHADO")
				.notasRepartidor("En camino")
				.numeroIntento(1)
				.build());

			administradorDeEntidad.flush();
			administradorDeEntidad.clear();

			assertTrue(repositorioRol.buscarPorCodigo(CodigoRol.ADMINISTRADOR).isPresent());
			assertTrue(repositorioCliente.buscarPorId(cliente.getId()).isPresent());
			assertTrue(repositorioEstadoPedido.buscarPorCodigo(CodigoEstadoPedido.PENDIENTE).isPresent());
			assertEquals(CodigoEstadoPedido.PENDIENTE, repositorioEstadoPedido.listarOrdenados().getFirst().getCodigoEstado());

			assertTrue(repositorioPersonal.buscarPorRol().stream().anyMatch(persona -> persona.getId().equals(cocinero.getId())));
			assertTrue(repositorioPersonal.buscarPorRol(CodigoRol.COCINERO).stream().anyMatch(persona -> persona.getId().equals(cocinero.getId())));
			assertTrue(
				repositorioPersonal.buscarRepartidoresDisponibles().stream()
					.anyMatch(persona -> persona.getId().equals(repartidorDisponible.getId()))
			);
			assertFalse(
				repositorioPersonal.buscarRepartidoresDisponibles().stream()
					.anyMatch(persona -> persona.getId().equals(repartidorOcupado.getId()))
			);

			assertTrue(repositorioProducto.buscarPorId(productoDisponible.getId()).isPresent());
			assertTrue(repositorioProducto.buscarPorCodigo(productoDisponible.getCodigoProducto()).isPresent());
			assertTrue(
				repositorioProducto.buscarDisponibles().stream()
					.anyMatch(producto -> producto.getId().equals(productoDisponible.getId()))
			);
			assertTrue(
				repositorioProducto.buscarDisponiblesPorCategoria(CategoriaProducto.PLATO_FUERTE).stream()
					.anyMatch(producto -> producto.getId().equals(productoDisponible.getId()))
			);

			assertTrue(repositorioPedido.buscarPorId(pedidoPrioritario.getId()).isPresent());
			assertTrue(repositorioPedido.buscarPorCodigo(pedidoPrioritario.getCodigoPedido()).isPresent());
			assertTrue(
				repositorioPedido.buscarPorEstado(CodigoEstadoPedido.PENDIENTE).stream()
					.anyMatch(pedido -> pedido.getId().equals(pedidoNormal.getId()))
			);

			List<String> ordenPendientesDemo = repositorioPedido.buscarPendientesParaCocinero().stream()
				.map(PedidoCliente::getCodigoPedido)
				.filter(Set.of(pedidoPrioritario.getCodigoPedido(), pedidoNormal.getCodigoPedido())::contains)
				.toList();

			assertEquals(List.of(pedidoPrioritario.getCodigoPedido(), pedidoNormal.getCodigoPedido()), ordenPendientesDemo);

			List<ReglaTransicionEstadoPedido> transicionesCocinero =
				repositorioReglas.buscarTransicionesValidas(CodigoRol.COCINERO, CodigoEstadoPedido.PENDIENTE);
			assertTrue(
				transicionesCocinero.stream()
					.anyMatch(regla -> regla.getEstadoDestino().getCodigoEstado() == CodigoEstadoPedido.EN_PREPARACION)
			);

			assertTrue(repositorioEntrega.buscarPorId(entregaActiva.getId()).isPresent());
			assertTrue(
				repositorioEntrega.buscarPorPedidoId(pedidoEnRuta.getId()).stream()
					.anyMatch(entrega -> entrega.getRepartidor().getId().equals(repartidorOcupado.getId()))
			);

			Personal repartidorActualizado = repositorioPersonal.buscarPorIdConRol(repartidorDisponible.getId()).orElseThrow();
			repartidorActualizado.setTelefono("0981999999");
			repositorioPersonal.actualizar(repartidorActualizado);

			Producto productoActualizado = repositorioProducto.buscarPorId(productoDisponible.getId()).orElseThrow();
			productoActualizado.setDescripcion("Producto disponible actualizado desde la demo");
			repositorioProducto.actualizar(productoActualizado);

			PedidoCliente pedidoActualizado = repositorioPedido.buscarPorId(pedidoPrioritario.getId()).orElseThrow();
			pedidoActualizado.setEstadoActual(estadoEnPreparacion);
			pedidoActualizado.setEstadoActualCambiadoEn(ahora.plusSeconds(300));
			repositorioPedido.actualizar(pedidoActualizado);

			Entrega entregaActualizada = repositorioEntrega.buscarPorId(entregaActiva.getId()).orElseThrow();
			entregaActualizada.setEstadoEntrega("ENTREGADO");
			entregaActualizada.setEntregadoEn(ahora.plusSeconds(900));
			entregaActualizada.setConfirmadoPorClienteEn(ahora.plusSeconds(930));
			entregaActualizada.setNombreReceptor(cliente.getNombreCompleto());
			repositorioEntrega.actualizar(entregaActualizada);

			administradorDeEntidad.flush();

			assertNotNull(cliente.getId());
			assertNotNull(direccionCliente.getId());
			assertNotNull(administrador.getId());
			assertNotNull(estadoEntregado.getId());
			assertNotNull(productoDisponible.getId());
			assertNotNull(pedidoPrioritario.getId());
			assertNotNull(entregaActiva.getId());

			transaccion.commit();
		} catch (RuntimeException exception) {
			if (transaccion != null && transaccion.isActive()) {
				transaccion.rollback();
			}

			if (esFallaDeConexion(exception)) {
				throw new TestAbortedException("RepositorioTest requiere MySQL disponible segun persistence.xml", exception);
			}

			throw exception;
		} finally {
			ConfiguracionBaseDatos.cerrar();
		}
	}

	private static boolean esFallaDeConexion(Throwable error) {
		Throwable actual = error;

		while (actual != null) {
			String mensaje = actual.getMessage();

			if (mensaje != null && (
				mensaje.contains("Communications link failure")
					|| mensaje.contains("Connection refused")
					|| mensaje.contains("Failed to initialize pool")
			)) {
				return true;
			}

			actual = actual.getCause();
		}

		return false;
	}

	private static Rol obtenerOCrearRol(RepositorioRol repositorioRol, CodigoRol codigoRol) {
		return repositorioRol.buscarPorCodigo(codigoRol)
			.orElseGet(() -> repositorioRol.guardar(Rol.builder().codigoRol(codigoRol).build()));
	}

	private static EstadoPedido obtenerOCrearEstado(
		RepositorioEstadoPedido repositorioEstadoPedido,
		CodigoEstadoPedido codigoEstado,
		String nombreEstado,
		int ordenEstado,
		boolean finalizado
	) {
		return repositorioEstadoPedido.buscarPorCodigo(codigoEstado)
			.orElseGet(() -> repositorioEstadoPedido.guardar(EstadoPedido.builder()
				.codigoEstado(codigoEstado)
				.nombreEstado(nombreEstado)
				.ordenEstado(ordenEstado)
				.finalizado(finalizado)
				.build()));
	}

	private static void asegurarRegla(
		RepositorioReglaTransicionEstadoPedido repositorioReglas,
		RepositorioRol repositorioRol,
		RepositorioEstadoPedido repositorioEstadoPedido,
		CodigoRol codigoRol,
		CodigoEstadoPedido estadoOrigen,
		CodigoEstadoPedido estadoDestino
	) {
		boolean existeRegla = repositorioReglas.buscarTransicionesValidas(codigoRol, estadoOrigen).stream()
			.anyMatch(regla -> regla.getEstadoDestino().getCodigoEstado() == estadoDestino);

		if (existeRegla) {
			return;
		}

		repositorioReglas.guardar(ReglaTransicionEstadoPedido.builder()
			.estadoOrigen(repositorioEstadoPedido.buscarPorCodigo(estadoOrigen).orElseThrow())
			.estadoDestino(repositorioEstadoPedido.buscarPorCodigo(estadoDestino).orElseThrow())
			.rol(repositorioRol.buscarPorCodigo(codigoRol).orElseThrow())
			.activa(true)
			.build());
	}

	private static PedidoCliente crearPedido(
		Cliente cliente,
		Personal administrador,
		EstadoPedido estadoPedido,
		DireccionCliente direccionCliente,
		String etiqueta,
		boolean prioritario,
		Instant creadoEn
	) {
		return PedidoCliente.builder()
			.codigoPedido(GeneradorCodigoPedido.generar())
			.cliente(cliente)
			.registradoPorPersonal(administrador)
			.estadoActual(estadoPedido)
			.direccionEntrega(direccionCliente)
			.snapshotDireccionEntrega("Av. Siempre Viva y Calle 1, casa 123, Quito")
			.instruccionesEntrega("Llamar al llegar")
			.subtotal(new BigDecimal("18.00"))
			.impuesto(new BigDecimal("2.16"))
			.descuento(BigDecimal.ZERO)
			.recargoDireccion(new BigDecimal("1.50"))
			.total(new BigDecimal("21.66"))
			.codigoDescuento(null)
			.prioritario(prioritario)
			.notasGenerales("Pedido demo " + etiqueta)
			.creadoEn(creadoEn)
			.entregaEstimadaEn(creadoEn.plusSeconds(1800))
			.estadoActualCambiadoEn(creadoEn)
			.build();
	}
}
