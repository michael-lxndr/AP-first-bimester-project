package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class RepositorioRol {
	private final EntityManager entityManager;

	public RepositorioRol(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Optional<Rol> findByCode(CodigoRol codigoRol) {
		try {
			Rol role = entityManager
				.createQuery("select r from Rol r where r.codigoRol = :codigoRol", Rol.class)
				.setParameter("codigoRol", codigoRol)
				.getSingleResult();

			return Optional.of(role);
		} catch (NoResultException exception) {
			return Optional.empty();
		}
	}

	public Rol save(Rol role) {
		entityManager.persist(role);
		return role;
	}
}
