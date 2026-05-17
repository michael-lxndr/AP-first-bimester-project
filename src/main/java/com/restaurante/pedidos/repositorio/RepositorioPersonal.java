package com.restaurante.pedidos.repositorio;

import com.restaurante.pedidos.dominio.entidad.Personal;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RepositorioPersonal {
	private final EntityManager entityManager;

	public RepositorioPersonal(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	// ?	JPQL (Java Persistence Query Language)
	public List<Personal> findAllWithRol() {
		return entityManager
			.createQuery("select s from Personal s join fetch s.rol order by s.id desc", Personal.class)
			.getResultList();
	}

	public Optional<Personal> findByIdWithRol(Long id) {
		List<Personal> personal = entityManager
			.createQuery("select s from Personal s join fetch s.rol where s.id = :id", Personal.class)
			.setParameter("id", id)
			.getResultList();

		return personal.stream().findFirst();
	}

	public Personal save(Personal personal) {
		entityManager.persist(personal);
		return personal;
	}

	public Personal update(Personal personal) {
		return entityManager.merge(personal);
	}

	public void delete(Personal personal) {
		Personal managedPersonal = entityManager.contains(personal) ? personal : entityManager.merge(personal);
		entityManager.remove(managedPersonal);
	}
}
