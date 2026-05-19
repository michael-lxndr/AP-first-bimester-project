package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.entidad.EstadoPedido;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositorioEstadoPedido {
	private final EntityManager administradorDeEntidad;

	public RepositorioEstadoPedido(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	public Optional<EstadoPedido> buscarPorId(Long id) {
		return Optional.ofNullable(administradorDeEntidad.find(EstadoPedido.class, id));
	}

	public Optional<EstadoPedido> buscarPorCodigo(CodigoEstadoPedido codigo) {
		return administradorDeEntidad
			.createQuery("""
				SELECT e FROM EstadoPedido e WHERE e.codigoEstado = :codigo
			""", EstadoPedido.class)
			.setParameter("codigo", codigo)
			.getResultList()
			.stream()
			.findFirst();
	}

	public List<EstadoPedido> listarOrdenados() {
		return administradorDeEntidad
			.createQuery("""
				SELECT e FROM EstadoPedido e ORDER BY e.ordenEstado ASC
			""", EstadoPedido.class)
			.getResultList();
	}

	public EstadoPedido guardar(EstadoPedido estadoPedido) {
		administradorDeEntidad.persist(estadoPedido);
		return estadoPedido;
	}
}
