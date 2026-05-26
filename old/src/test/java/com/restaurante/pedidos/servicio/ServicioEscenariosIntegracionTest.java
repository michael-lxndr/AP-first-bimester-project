package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.GeneradorCodigoPedido;
import com.restaurante.pedidos.dominio.entidad.*;
import com.restaurante.pedidos.persistencia.controlador.PersonalJpaController;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class ServicioEscenariosIntegracionTest {
	@Test
	void creaPedidoConEstadoPendienteYConsultaPorCodigo() {
		Fixture fixture = new Fixture();
		PedidoCliente pedido = fixture.nuevoPedidoBase();

		ServicioPedido servicioPedido = new ServicioPedido();
		PedidoCliente creado = servicioPedido.crearPedido(pedido);

		assertNotNull(creado.getId());
		assertEquals(CodigoEstadoPedido.PENDIENTE, creado.getEstadoActual().getCodigoEstado());

		PedidoCliente buscado = servicioPedido.buscarPedidoPorCodigo(creado.getCodigoPedido());
		assertNotNull(buscado);
		assertEquals(creado.getCodigoPedido(), buscado.getCodigoPedido());
		assertEquals("PENDIENTE", servicioPedido.consultarEstadoPorCodigo(creado.getCodigoPedido()));
	}

	@Test
	void asignaRepartidorElegibleYAvanzaAEnCamino() {
		Fixture fixture = new Fixture();
		PedidoCliente pedidoListo = fixture.nuevoPedidoConEstado(CodigoEstadoPedido.LISTO);

		ServicioPedido servicioPedido = new ServicioPedido();
		PedidoCliente creado = servicioPedido.crearPedido(pedidoListo);

		ServicioCocina servicioCocina = new ServicioCocina();
		servicioCocina.iniciarPreparacion(creado.getCodigoPedido(), CodigoRol.COCINERO);
		servicioCocina.marcarListo(creado.getCodigoPedido(), CodigoRol.COCINERO);

		PersonalJpaController personalController = new PersonalJpaController(ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades());
		List<Personal> disponibles = personalController.findRepartidoresDisponibles();
		assertFalse(disponibles.isEmpty());

		ServicioEntrega servicioEntrega = new ServicioEntrega();
		PedidoCliente enCamino = servicioEntrega.iniciarEntrega(creado.getCodigoPedido(), CodigoRol.REPARTIDOR);
		assertNotNull(enCamino.getId());
		assertEquals("EN_CAMINO", servicioPedido.consultarEstadoPorCodigo(creado.getCodigoPedido()));
	}

	@Test
	void simulaPedidoHastaEntregadoConRutaCompleta() throws ExecutionException, InterruptedException {
		Fixture fixture = new Fixture();
		PedidoCliente pedido = fixture.nuevoPedidoBase();
		ServicioPedido servicioPedido = new ServicioPedido();
		PedidoCliente creado = servicioPedido.crearPedido(pedido);

		ServicioSimulacion simulacion = new ServicioSimulacion(
			new ServicioCocina(),
			new ServicioEntrega(),
			() -> Duration.ZERO,
			() -> Duration.ZERO,
			delay -> {}
		);

		List<String> historial = simulacion.simularPedido(creado.getCodigoPedido()).get();
		assertEquals(List.of("EN_PREPARACION", "LISTO", "EN_CAMINO", "ENTREGADO"), historial);
		assertEquals("ENTREGADO", servicioPedido.consultarEstadoPorCodigo(creado.getCodigoPedido()));
	}

	private static final class Fixture {
		private final Cliente cliente;
		private final DireccionCliente direccion;
		private final Personal admin;

		private Fixture() {
			try (var em = ConfiguracionBaseDatos.crearAdministradorDeEntidad()) {
				var tx = em.getTransaction();
				tx.begin();
				Rol adminRol = asegurarRol(em, CodigoRol.ADMINISTRADOR);
				asegurarRol(em, CodigoRol.COCINERO);
				asegurarRol(em, CodigoRol.REPARTIDOR);
				asegurarEstadosYReglas(em);

				this.cliente = new Cliente();
				cliente.setNombreCompleto("Cliente Escenario " + System.nanoTime());
				cliente.setTelefono("0991234567");
				cliente.setCorreoElectronico("escenario@test.com");
				cliente.setActivo(true);
				cliente.setCreadoEn(Instant.now());
				em.persist(cliente);

				this.direccion = new DireccionCliente();
				direccion.setCliente(cliente);
				direccion.setAlias("Casa");
				direccion.setCallePrincipal("Av. Principal");
				direccion.setCiudad("Quito");
				direccion.setProvincia("Pichincha");
				direccion.setPais("Ecuador");
				direccion.setPrincipal(true);
				direccion.setActiva(true);
				em.persist(direccion);

				this.admin = new Personal();
				admin.setRol(adminRol);
				admin.setNombreCompleto("Admin Escenario");
				admin.setCorreoElectronico("admin.escenario@test.com");
				admin.setNombreUsuario("admin_escenario_" + System.nanoTime());
				admin.setActivo(true);
				admin.setCreadoEn(Instant.now());
				em.persist(admin);

				asegurarRepartidorDisponible(em);
				tx.commit();
			}
		}

		private PedidoCliente nuevoPedidoBase() {
			PedidoCliente pedido = new PedidoCliente();
			pedido.setCodigoPedido(GeneradorCodigoPedido.generar());
			pedido.setCliente(cliente);
			pedido.setRegistradoPorPersonal(admin);
			pedido.setDireccionEntrega(direccion);
			pedido.setSnapshotDireccionEntrega("Av. Principal");
			pedido.setSubtotal(new BigDecimal("10.00"));
			pedido.setImpuesto(new BigDecimal("1.20"));
			pedido.setDescuento(BigDecimal.ZERO);
			pedido.setRecargoDireccion(BigDecimal.ZERO);
			pedido.setTotal(new BigDecimal("11.20"));
			pedido.setPrioritario(false);
			pedido.setCreadoEn(Instant.now());
			pedido.setEstadoActualCambiadoEn(Instant.now());
			return pedido;
		}

		private PedidoCliente nuevoPedidoConEstado(CodigoEstadoPedido ignored) {
			return nuevoPedidoBase();
		}

		private static Rol asegurarRol(jakarta.persistence.EntityManager em, CodigoRol codigoRol) {
			var roles = em.createQuery("SELECT r FROM Rol r WHERE r.codigoRol = :codigo", Rol.class)
				.setParameter("codigo", codigoRol)
				.setMaxResults(1)
				.getResultList();
			if (!roles.isEmpty()) return roles.getFirst();
			Rol rol = new Rol();
			rol.setCodigoRol(codigoRol);
			em.persist(rol);
			return rol;
		}

		private static EstadoPedido asegurarEstado(jakarta.persistence.EntityManager em, CodigoEstadoPedido codigo, int orden, boolean finalizado) {
			var estados = em.createQuery("SELECT e FROM EstadoPedido e WHERE e.codigoEstado = :codigo", EstadoPedido.class)
				.setParameter("codigo", codigo)
				.setMaxResults(1)
				.getResultList();
			if (!estados.isEmpty()) return estados.getFirst();
			EstadoPedido estado = new EstadoPedido();
			estado.setCodigoEstado(codigo);
			estado.setNombreEstado(codigo.name());
			estado.setOrdenEstado(orden);
			estado.setFinalizado(finalizado);
			em.persist(estado);
			return estado;
		}

		private static void asegurarEstadosYReglas(jakarta.persistence.EntityManager em) {
			EstadoPedido pendiente = asegurarEstado(em, CodigoEstadoPedido.PENDIENTE, 1, false);
			EstadoPedido prep = asegurarEstado(em, CodigoEstadoPedido.EN_PREPARACION, 2, false);
			EstadoPedido listo = asegurarEstado(em, CodigoEstadoPedido.LISTO, 3, false);
			EstadoPedido camino = asegurarEstado(em, CodigoEstadoPedido.EN_CAMINO, 4, false);
			EstadoPedido entregado = asegurarEstado(em, CodigoEstadoPedido.ENTREGADO, 5, true);
			Rol cocinero = asegurarRol(em, CodigoRol.COCINERO);
			Rol repartidor = asegurarRol(em, CodigoRol.REPARTIDOR);
			asegurarRegla(em, pendiente, prep, cocinero);
			asegurarRegla(em, prep, listo, cocinero);
			asegurarRegla(em, listo, camino, repartidor);
			asegurarRegla(em, camino, entregado, repartidor);
		}

		private static void asegurarRegla(jakarta.persistence.EntityManager em, EstadoPedido origen, EstadoPedido destino, Rol rol) {
			Long count = em.createQuery("""
				SELECT COUNT(r) FROM ReglaTransicionEstadoPedido r
				WHERE r.estadoOrigen = :origen AND r.estadoDestino = :destino AND r.rol = :rol
			""", Long.class).setParameter("origen", origen).setParameter("destino", destino).setParameter("rol", rol).getSingleResult();
			if (count > 0) return;
			ReglaTransicionEstadoPedido regla = new ReglaTransicionEstadoPedido();
			regla.setEstadoOrigen(origen);
			regla.setEstadoDestino(destino);
			regla.setRol(rol);
			regla.setActiva(true);
			em.persist(regla);
		}

		private static void asegurarRepartidorDisponible(jakarta.persistence.EntityManager em) {
			Rol rol = asegurarRol(em, CodigoRol.REPARTIDOR);
			Personal repartidor = new Personal();
			repartidor.setRol(rol);
			repartidor.setNombreCompleto("Repartidor Disponible " + System.nanoTime());
			repartidor.setCorreoElectronico("repartidor.disponible@test.com");
			repartidor.setNombreUsuario("rep_disp_" + System.nanoTime());
			repartidor.setActivo(true);
			repartidor.setCreadoEn(Instant.now());
			em.persist(repartidor);
		}
	}
}
