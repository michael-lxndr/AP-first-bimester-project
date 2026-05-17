package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.entidad.Cliente;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class RepositorioCliente {
	private final EntityManager entityManager;

	public RepositorioCliente(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void save(Cliente cliente) {
		entityManager.persist(cliente);
	}

	public Optional<Cliente> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Cliente.class, id));
	}
}
