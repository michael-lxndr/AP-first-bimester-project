package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.entidad.PedidoCliente;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositorioPedido {
	private final EntityManager administradorDeEntidad;

	public RepositorioPedido(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	public Optional<PedidoCliente> buscarPorCodigo(String codigo) {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM PedidoCliente p
				JOIN FETCH p.estadoActual
				WHERE p.codigoPedido = :codigo
			""", PedidoCliente.class)
			.setParameter("codigo", codigo)
			.getResultList()
			.stream()
			.findFirst();
	}

	public List<PedidoCliente> buscarPorEstado(CodigoEstadoPedido estado) {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM PedidoCliente p
				JOIN FETCH p.estadoActual e
				WHERE e.codigoEstado = :estado
				ORDER BY p.creadoEn ASC
			""", PedidoCliente.class)
			.setParameter("estado", estado)
			.getResultList();
	}

	public List<PedidoCliente> buscarPendientesParaCocinero() {
		return administradorDeEntidad
			.createQuery("""
				SELECT p
				FROM PedidoCliente p
				JOIN FETCH p.estadoActual e
				WHERE e.codigoEstado = :estadoPendiente
				ORDER BY p.prioritario DESC, p.creadoEn ASC
			""", PedidoCliente.class)
			.setParameter("estadoPendiente", CodigoEstadoPedido.PENDIENTE)
			.getResultList();
	}

	public PedidoCliente guardar(PedidoCliente pedido) {
		administradorDeEntidad.persist(pedido);
		return pedido;
	}

	public PedidoCliente actualizar(PedidoCliente pedido) {
		return administradorDeEntidad.merge(pedido);
	}

	public Optional<PedidoCliente> buscarPorId(Long id) {
		return Optional.ofNullable(administradorDeEntidad.find(PedidoCliente.class, id));
	}
}
