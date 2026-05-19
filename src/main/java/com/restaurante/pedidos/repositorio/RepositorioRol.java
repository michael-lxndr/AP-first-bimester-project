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

	public Optional<Rol> buscarPorCodigo(CodigoRol codigoRol) {
		try {
			Rol rol = entityManager
				.createQuery("""
					 SELECT r FROM Rol r WHERE r.codigoRol = :codigoRol
				""", Rol.class)
				.setParameter("codigoRol", codigoRol)
				.getSingleResult();

			return Optional.of(rol);
		} catch (NoResultException exception) {
			return Optional.empty();
		}
	}

	public Rol guardar(Rol rol) {
		entityManager.persist(rol);
		return rol;
	}
}
