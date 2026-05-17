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
		List<Personal> staff = entityManager
			.createQuery("select s from Personal s join fetch s.rol where s.id = :id", Personal.class)
			.setParameter("id", id)
			.getResultList();

		return staff.stream().findFirst();
	}

	public Personal save(Personal staff) {
		entityManager.persist(staff);
		return staff;
	}

	public Personal update(Personal staff) {
		return entityManager.merge(staff);
	}

	public void delete(Personal staff) {
		Personal managedPersonal = entityManager.contains(staff) ? staff : entityManager.merge(staff);
		entityManager.remove(managedPersonal);
	}
}
