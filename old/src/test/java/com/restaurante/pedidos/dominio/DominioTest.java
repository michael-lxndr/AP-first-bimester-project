package com.restaurante.pedidos.dominio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.entidad.Cliente;
import com.restaurante.pedidos.dominio.entidad.EstadoPedido;
import com.restaurante.pedidos.dominio.entidad.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DominioTest {
	@Test
	void entidadesBasicasPersistenSinLombok() {
		EntityTransaction transaccion = null;
		try (EntityManager em = ConfiguracionBaseDatos.crearAdministradorDeEntidad()) {
			transaccion = em.getTransaction();
			transaccion.begin();

			Rol rol = new Rol();
			rol.setCodigoRol(CodigoRol.ADMINISTRADOR);
			em.persist(rol);

			EstadoPedido estado = new EstadoPedido();
			estado.setCodigoEstado(CodigoEstadoPedido.PENDIENTE);
			estado.setNombreEstado("Pendiente");
			estado.setOrdenEstado(1);
			estado.setFinalizado(false);
			em.persist(estado);

			Cliente cliente = new Cliente();
			cliente.setNombreCompleto("Cliente Dominio");
			cliente.setTelefono("0990000000");
			cliente.setCorreoElectronico("dominio@test.com");
			cliente.setActivo(true);
			cliente.setCreadoEn(Instant.now());
			em.persist(cliente);

			em.flush();
			assertNotNull(rol.getId());
			assertNotNull(estado.getId());
			assertNotNull(cliente.getId());

			transaccion.commit();
		} catch (RuntimeException e) {
			if (transaccion != null && transaccion.isActive()) {
				transaccion.rollback();
			}
			throw e;
		} finally {
			ConfiguracionBaseDatos.cerrar();
		}
	}
}
