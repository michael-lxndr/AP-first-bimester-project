package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.entidad.Cliente;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class RepositorioCliente {
	private final EntityManager administradorDeEntidad;

	public RepositorioCliente(EntityManager administradorDeEntidad) {
		this.administradorDeEntidad = administradorDeEntidad;
	}

	public void guardar(Cliente cliente) {
		administradorDeEntidad.persist(cliente);
	}

	public Optional<Cliente> buscarPorId(Long id) {
		return Optional.ofNullable(administradorDeEntidad.find(Cliente.class, id));
	}
}
