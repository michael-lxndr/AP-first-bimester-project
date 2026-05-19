package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.ReglaTransicionEstadoPedido;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositorioReglaTransicionEstadoPedido {
	private final EntityManager administradorDeEntidad;

	public RepositorioReglaTransicionEstadoPedido(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	public Optional<ReglaTransicionEstadoPedido> buscarPorId(Long id) {
		return Optional.ofNullable(administradorDeEntidad.find(ReglaTransicionEstadoPedido.class, id));
	}

	public List<ReglaTransicionEstadoPedido> buscarTransicionesValidas(
		CodigoRol rol,
		CodigoEstadoPedido estadoActual
	) {
		return administradorDeEntidad
			.createQuery("""
				SELECT r
				FROM ReglaTransicionEstadoPedido r
				JOIN FETCH r.estadoOrigen origen
				JOIN FETCH r.estadoDestino destino
				JOIN FETCH r.rol rolPermitido
				WHERE rolPermitido.codigoRol = :rol
				  AND origen.codigoEstado = :estadoActual
				  AND r.activa = true
				ORDER BY destino.ordenEstado ASC
			""", ReglaTransicionEstadoPedido.class)
			.setParameter("rol", rol)
			.setParameter("estadoActual", estadoActual)
			.getResultList();
	}

	public ReglaTransicionEstadoPedido guardar(ReglaTransicionEstadoPedido regla) {
		administradorDeEntidad.persist(regla);
		return regla;
	}
}
