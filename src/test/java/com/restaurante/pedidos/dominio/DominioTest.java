package com.restaurante.pedidos.dominio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.entidad.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DominioTest {
	@Test
	void datosDeInicioDeTodasLasEntidades() {
		EntityManager entityManager = ConfiguracionBaseDatos.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		Instant ahora = Instant.now();

		try {
			transaction.begin();

			Rol rolAdministrador = Rol.builder()
				.codigoRol(CodigoRol.ADMINISTRADOR)
				.build();
			entityManager.persist(rolAdministrador);

			Rol rolCocinero = Rol.builder()
				.codigoRol(CodigoRol.COCINERO)
				.build();
			entityManager.persist(rolCocinero);

			Rol rolRepartidor = Rol.builder()
				.codigoRol(CodigoRol.REPARTIDOR)
				.build();
			entityManager.persist(rolRepartidor);

			EstadoPedido pendiente = EstadoPedido.builder()
				.codigoEstado(CodigoEstadoPedido.PENDIENTE)
				.nombreEstado("Pendiente")
				.ordenEstado(1)
				.finalizado(false)
				.build();
			entityManager.persist(pendiente);

			EstadoPedido enPreparacion = EstadoPedido.builder()
				.codigoEstado(CodigoEstadoPedido.EN_PREPARACION)
				.nombreEstado("En preparacion")
				.ordenEstado(2)
				.finalizado(false)
				.build();
			entityManager.persist(enPreparacion);

			EstadoPedido listo = EstadoPedido.builder()
				.codigoEstado(CodigoEstadoPedido.LISTO)
				.nombreEstado("Listo")
				.ordenEstado(3)
				.finalizado(false)
				.build();
			entityManager.persist(listo);

			EstadoPedido enCamino = EstadoPedido.builder()
				.codigoEstado(CodigoEstadoPedido.EN_CAMINO)
				.nombreEstado("En camino")
				.ordenEstado(4)
				.finalizado(false)
				.build();
			entityManager.persist(enCamino);

			EstadoPedido entregado = EstadoPedido.builder()
				.codigoEstado(CodigoEstadoPedido.ENTREGADO)
				.nombreEstado("Entregado")
				.ordenEstado(5)
				.finalizado(true)
				.build();
			entityManager.persist(entregado);

			Cliente cliente = Cliente.builder()
				.nombreCompleto("Juan Perez")
				.telefono("0999999999")
				.correoElectronico("juan.perez@test.com")
				.activo(true)
				.creadoEn(ahora)
				.build();
			entityManager.persist(cliente);

			DireccionCliente direccionCliente = DireccionCliente.builder()
				.cliente(cliente)
				.alias("Casa")
				.callePrincipal("Av. Principal")
				.calleSecundaria("Calle Secundaria")
				.numeroCasa("123")
				.referencia("Porton negro")
				.codigoPostal("170101")
				.ciudad("Quito")
				.provincia("Pichincha")
				.pais("Ecuador")
				.principal(true)
				.activa(true)
				.build();
			entityManager.persist(direccionCliente);

			Personal administrador = Personal.builder()
				.rol(rolAdministrador)
				.nombreCompleto("Ana Admin")
				.telefono("0988888888")
				.correoElectronico("admin@test.com")
				.nombreUsuario("ana.admin")
				.activo(true)
				.creadoEn(ahora)
				.build();
			entityManager.persist(administrador);

			Personal cocinero = Personal.builder()
				.rol(rolCocinero)
				.nombreCompleto("Carlos Cocina")
				.telefono("0977777777")
				.correoElectronico("cocina@test.com")
				.nombreUsuario("carlos.cocina")
				.activo(true)
				.creadoEn(ahora.plusSeconds(30))
				.build();
			entityManager.persist(cocinero);

			Personal repartidor = Personal.builder()
				.rol(rolRepartidor)
				.nombreCompleto("Rene Reparto")
				.telefono("0966666666")
				.correoElectronico("reparto@test.com")
				.nombreUsuario("rene.reparto")
				.activo(true)
				.creadoEn(ahora.plusSeconds(60))
				.build();
			entityManager.persist(repartidor);

			Producto productoPrincipal = Producto.builder()
				.codigoProducto("PROD-001")
				.nombreProducto("Pizza Especial")
				.descripcion("Pizza familiar con ingredientes mixtos")
				.precioUnitario(new BigDecimal("12.50"))
				.costoProduccion(new BigDecimal("7.80"))
				.categoria(CategoriaProducto.PLATO_FUERTE)
				.tiempoPreparacionMinutos(25)
				.disponible(true)
				.urlImagen("https://example.com/pizza.jpg")
				.creadoEn(ahora)
				.build();
			entityManager.persist(productoPrincipal);

			Producto bebida = Producto.builder()
				.codigoProducto("PROD-002")
				.nombreProducto("Limonada")
				.descripcion("Bebida natural")
				.precioUnitario(new BigDecimal("2.50"))
				.costoProduccion(new BigDecimal("0.90"))
				.categoria(CategoriaProducto.BEBIDA)
				.tiempoPreparacionMinutos(5)
				.disponible(true)
				.urlImagen("https://example.com/limonada.jpg")
				.creadoEn(ahora.plusSeconds(90))
				.build();
			entityManager.persist(bebida);

			PedidoCliente pedido = PedidoCliente.builder()
				.codigoPedido(GeneradorCodigoPedido.generar())
				.cliente(cliente)
				.registradoPorPersonal(administrador)
				.estadoActual(enCamino)
				.direccionEntrega(direccionCliente)
				.snapshotDireccionEntrega("Av. Principal y Calle Secundaria, casa 123, Quito")
				.instruccionesEntrega("Tocar el timbre una vez")
				.subtotal(new BigDecimal("27.50"))
				.impuesto(new BigDecimal("3.30"))
				.descuento(new BigDecimal("2.00"))
				.recargoDireccion(new BigDecimal("1.50"))
				.total(new BigDecimal("30.30"))
				.codigoDescuento("BIENVENIDA")
				.prioritario(false)
				.notasGenerales("Pedido de prueba para generar tablas")
				.creadoEn(ahora.plusSeconds(120))
				.entregaEstimadaEn(ahora.plusSeconds(3000))
				.estadoActualCambiadoEn(ahora.plusSeconds(180))
				.build();
			entityManager.persist(pedido);

			ItemPedido itemPrincipal = ItemPedido.builder()
				.pedido(pedido)
				.producto(productoPrincipal)
				.cantidad(2)
				.snapshotNombreProducto("Pizza Especial")
				.precioUnitario(new BigDecimal("12.50"))
				.totalLinea(new BigDecimal("25.00"))
				.notaEspecial("Sin cebolla")
				.listo(true)
				.build();
			entityManager.persist(itemPrincipal);

			ItemPedido itemBebida = ItemPedido.builder()
				.pedido(pedido)
				.producto(bebida)
				.cantidad(1)
				.snapshotNombreProducto("Limonada")
				.precioUnitario(new BigDecimal("2.50"))
				.totalLinea(new BigDecimal("2.50"))
				.notaEspecial("Poco hielo")
				.listo(true)
				.build();
			entityManager.persist(itemBebida);

			HistorialEstadoPedido historial = HistorialEstadoPedido.builder()
				.pedido(pedido)
				.estadoOrigen(listo)
				.estadoDestino(enCamino)
				.cambiadoPorPersonal(cocinero)
				.cambiadoEn(ahora.plusSeconds(180))
				.notas("Pedido entregado al repartidor")
				.build();
			entityManager.persist(historial);

			ReglaTransicionEstadoPedido regla = ReglaTransicionEstadoPedido.builder()
				.estadoOrigen(listo)
				.estadoDestino(enCamino)
				.rol(rolRepartidor)
				.activa(true)
				.build();
			entityManager.persist(regla);

			Entrega entrega = Entrega.builder()
				.pedido(pedido)
				.repartidor(repartidor)
				.despachadoEn(ahora.plusSeconds(180))
				.entregadoEn(ahora.plusSeconds(2400))
				.nombreReceptor("Juan Perez")
				.confirmadoPorClienteEn(ahora.plusSeconds(2460))
				.notasConfirmacionCliente("Recibido sin novedades")
				.estadoEntrega("ENTREGADO")
				.notasRepartidor("Entrega completada en el primer intento")
				.numeroIntento(1)
				.build();
			entityManager.persist(entrega);

			entityManager.flush();

			assertNotNull(rolAdministrador.getId());
			assertNotNull(entregado.getId());
			assertNotNull(cliente.getId());
			assertNotNull(direccionCliente.getId());
			assertNotNull(administrador.getId());
			assertNotNull(repartidor.getId());
			assertNotNull(productoPrincipal.getId());
			assertNotNull(pedido.getId());
			assertNotNull(itemPrincipal.getId());
			assertNotNull(historial.getId());
			assertNotNull(regla.getId());
			assertNotNull(entrega.getId());

			transaction.commit();
		} catch (RuntimeException exception) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw exception;
		} finally {
			entityManager.close();
			ConfiguracionBaseDatos.close();
		}
	}
}
