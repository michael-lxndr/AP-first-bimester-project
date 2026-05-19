package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.entidad.Entrega;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class RepositorioEntrega {
	private final EntityManager administradorDeEntidad;

	public RepositorioEntrega(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	public Optional<Entrega> buscarPorId(Long id) {
		return Optional.ofNullable(administradorDeEntidad.find(Entrega.class, id));
	}

	public Optional<Entrega> buscarPorPedidoId(Long pedidoId) {
		return administradorDeEntidad
			.createQuery("""
				SELECT e
				FROM Entrega e
				JOIN FETCH e.pedido p
				JOIN FETCH e.repartidor r
				WHERE p.id = :pedidoId
			""", Entrega.class)
			.setParameter("pedidoId", pedidoId)
			.getResultList()
			.stream()
			.findFirst();
	}

	public Entrega guardar(Entrega entrega) {
		administradorDeEntidad.persist(entrega);
		return entrega;
	}

	public Entrega actualizar(Entrega entrega) {
		return administradorDeEntidad.merge(entrega);
	}
}
