package com.restaurante.pedidos.repository;

import com.restaurante.pedidos.domain.entity.Customer;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class CustomerRepository {
	private final EntityManager entityManager;

	public CustomerRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void save(Customer customer) {
		entityManager.persist(customer);
	}

	public Optional<Customer> findById(Long id) {
		return Optional.ofNullable(entityManager.find(Customer.class, id));
	}
}
