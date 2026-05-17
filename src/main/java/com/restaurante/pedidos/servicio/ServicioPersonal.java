package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.Rol;
import com.restaurante.pedidos.dominio.entidad.Personal;
import com.restaurante.pedidos.repositorio.RepositorioRol;
import com.restaurante.pedidos.repositorio.RepositorioPersonal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.Instant;
import java.util.List;

public class ServicioPersonal {
	public List<Personal> buscarTodos() {
		try (EntityManager entityManager = ConfiguracionBaseDatos.createEntityManager()) {
			return new RepositorioPersonal(entityManager).findAllWithRol();
		}
	}

	public Personal crear(CodigoRol roleCode, String fullName, String phone, String email, String username, boolean active) {
		return executeInTransaction(entityManager -> {
			Rol role = getOrCreateRol(entityManager, roleCode);

			Personal staff = Personal.builder()
				.rol(role)
				.fullName(fullName)
				.phone(phone)
				.email(email)
				.username(username)
				.isActive(active)
				.createdAt(Instant.now())
				.build();

			return new RepositorioPersonal(entityManager).save(staff);
		});
	}

	public Personal modificar(Long staffId, CodigoRol roleCode, String fullName, String phone, String email, String username, boolean active) {
		return executeInTransaction(entityManager -> {
			RepositorioPersonal staffRepository = new RepositorioPersonal(entityManager);
			Personal staff = staffRepository.findByIdWithRol(staffId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + staffId));
			Rol role = getOrCreateRol(entityManager, roleCode);

			staff.setRol(role);
			staff.setFullName(fullName);
			staff.setPhone(phone);
			staff.setEmail(email);
			staff.setUsername(username);
			staff.setIsActive(active);

			return staffRepository.update(staff);
		});
	}

	public void establecerActivo(Long staffId, boolean active) {
		executeInTransaction(entityManager -> {
			RepositorioPersonal staffRepository = new RepositorioPersonal(entityManager);
			Personal staff = staffRepository.findByIdWithRol(staffId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + staffId));

			staff.setIsActive(active);
			staffRepository.update(staff);
			return null;
		});
	}

	public void eliminar(Long staffId) {
		executeInTransaction(entityManager -> {
			RepositorioPersonal staffRepository = new RepositorioPersonal(entityManager);
			Personal staff = staffRepository.findByIdWithRol(staffId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + staffId));

			staffRepository.delete(staff);
			return null;
		});
	}

	private Rol getOrCreateRol(EntityManager entityManager, CodigoRol roleCode) {
		RepositorioRol roleRepository = new RepositorioRol(entityManager);

		return roleRepository.findByCode(roleCode)
			.orElseGet(() -> roleRepository.save(Rol.builder().codigoRol(roleCode).build()));
	}

	private <T> T executeInTransaction(TransactionWork<T> work) {
		try (EntityManager entityManager = ConfiguracionBaseDatos.createEntityManager()) {
			EntityTransaction transaction = entityManager.getTransaction();
			try {
				transaction.begin();
				T result = work.execute(entityManager);
				transaction.commit();
				return result;
			} catch (RuntimeException exception) {
				if (transaction.isActive()) {
					transaction.rollback();
				}

				throw exception;
			}
		}
	}

	@FunctionalInterface
	private interface TransactionWork<T> {
		T execute(EntityManager entityManager);
	}
}
