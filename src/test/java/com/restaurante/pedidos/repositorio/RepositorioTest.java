package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.GeneradorCodigoPedido;
import com.restaurante.pedidos.dominio.entidad.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioTest {
	@Test
	void repositoriosPrincipalesFuncionanConEntidadesSinLombok() {
		EntityTransaction tx = null;
		try (EntityManager em = ConfiguracionBaseDatos.crearAdministradorDeEntidad()) {
			tx = em.getTransaction();
			tx.begin();

			RepositorioRol repositorioRol = new RepositorioRol(em);
			RepositorioEstadoPedido repositorioEstado = new RepositorioEstadoPedido(em);
			RepositorioCliente repositorioCliente = new RepositorioCliente(em);
			RepositorioPedido repositorioPedido = new RepositorioPedido(em);

			Rol rol = repositorioRol.buscarPorCodigo(CodigoRol.ADMINISTRADOR).orElseGet(() -> {
				Rol nuevo = new Rol();
				nuevo.setCodigoRol(CodigoRol.ADMINISTRADOR);
				return repositorioRol.guardar(nuevo);
			});

			EstadoPedido pendiente = repositorioEstado.buscarPorCodigo(CodigoEstadoPedido.PENDIENTE).orElseGet(() -> {
				EstadoPedido estado = new EstadoPedido();
				estado.setCodigoEstado(CodigoEstadoPedido.PENDIENTE);
				estado.setNombreEstado("Pendiente");
				estado.setOrdenEstado(1);
				estado.setFinalizado(false);
				return repositorioEstado.guardar(estado);
			});

			Cliente cliente = new Cliente();
			cliente.setNombreCompleto("Cliente Repo");
			cliente.setTelefono("0991111111");
			cliente.setCorreoElectronico("repo@test.com");
			cliente.setActivo(true);
			cliente.setCreadoEn(Instant.now());
			repositorioCliente.guardar(cliente);

			DireccionCliente direccion = new DireccionCliente();
			direccion.setCliente(cliente);
			direccion.setAlias("Casa");
			direccion.setCallePrincipal("Av. A");
			direccion.setCiudad("Quito");
			direccion.setProvincia("Pichincha");
			direccion.setPais("Ecuador");
			direccion.setPrincipal(true);
			direccion.setActiva(true);
			em.persist(direccion);

			Personal personal = new Personal();
			personal.setRol(rol);
			personal.setNombreCompleto("Admin Repo");
			personal.setCorreoElectronico("admin.repo@test.com");
			personal.setNombreUsuario("admin_repo");
			personal.setActivo(true);
			personal.setCreadoEn(Instant.now());
			em.persist(personal);

			PedidoCliente pedido = new PedidoCliente();
			pedido.setCodigoPedido(GeneradorCodigoPedido.generar());
			pedido.setCliente(cliente);
			pedido.setRegistradoPorPersonal(personal);
			pedido.setEstadoActual(pendiente);
			pedido.setDireccionEntrega(direccion);
			pedido.setSnapshotDireccionEntrega("Av. A");
			pedido.setSubtotal(new BigDecimal("10.00"));
			pedido.setImpuesto(new BigDecimal("1.20"));
			pedido.setDescuento(BigDecimal.ZERO);
			pedido.setRecargoDireccion(BigDecimal.ZERO);
			pedido.setTotal(new BigDecimal("11.20"));
			pedido.setPrioritario(false);
			pedido.setCreadoEn(Instant.now());
			pedido.setEstadoActualCambiadoEn(Instant.now());
			repositorioPedido.guardar(pedido);

			em.flush();
			em.clear();

			assertTrue(repositorioRol.buscarPorCodigo(CodigoRol.ADMINISTRADOR).isPresent());
			assertTrue(repositorioEstado.buscarPorCodigo(CodigoEstadoPedido.PENDIENTE).isPresent());
			assertTrue(repositorioPedido.buscarPorCodigo(pedido.getCodigoPedido()).isPresent());
			assertEquals(CodigoEstadoPedido.PENDIENTE, repositorioPedido.buscarPorCodigo(pedido.getCodigoPedido()).orElseThrow().getEstadoActual().getCodigoEstado());

			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			ConfiguracionBaseDatos.cerrar();
		}
	}
}
