package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.Personal;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositorioPersonal {
	private final EntityManager administradorDeEntidad;

	public RepositorioPersonal(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	// ?	JPQL (Lenguaje de Consultas de Persistencia de Java)
	public List<Personal> buscarPorRol() {
		return administradorDeEntidad
			.createQuery("""
				SELECT p FROM Personal p JOIN FETCH p.rol ORDER BY p.id DESC
			""", Personal.class)
			.getResultList();
	}

	public List<Personal> buscarPorRol(CodigoRol rol) {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM Personal p
				JOIN FETCH p.rol r
				WHERE r.codigoRol = :rol
				ORDER BY p.id DESC
			""", Personal.class)
			.setParameter("rol", rol)
			.getResultList();
	}

	public List<Personal> buscarRepartidoresDisponibles() {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM Personal p
				JOIN FETCH p.rol r
				WHERE r.codigoRol = :rolRepartidor
				  AND p.activo = true
				  AND NOT EXISTS (
				    SELECT 1
				    FROM Entrega e
				    JOIN e.pedido pedido
				    JOIN pedido.estadoActual estadoPedido
				    WHERE e.repartidor = p
				      AND (
				        estadoPedido.codigoEstado = :estadoEnCamino
				        OR e.estadoEntrega IN :estadosEntregaActivos
				      )
				  )
				ORDER BY p.id DESC
			""", Personal.class)
			.setParameter("rolRepartidor", CodigoRol.REPARTIDOR)
			.setParameter("estadoEnCamino", CodigoEstadoPedido.EN_CAMINO)
			.setParameter("estadosEntregaActivos", List.of("DESPACHADO", "EN_TRANSITO"))
			.getResultList();
	}

	public Optional<Personal> buscarPorIdConRol(Long id) {
		List<Personal> personal = administradorDeEntidad
			.createQuery("""
				SELECT p FROM Personal p JOIN FETCH p.rol WHERE p.id = :id
			""", Personal.class)
			.setParameter("id", id)
			.getResultList();

		return personal.stream().findFirst();
	}

	public Personal guardar(Personal personal) {
		administradorDeEntidad.persist(personal);
		return personal;
	}

	public Personal actualizar(Personal personal) {
		return administradorDeEntidad.merge(personal);
	}

	public void borrar(Personal personal) {
		Personal personalAdministrado = administradorDeEntidad.contains(personal)
			? personal
			: administradorDeEntidad.merge(personal);

		administradorDeEntidad.remove(personalAdministrado);
	}
}
