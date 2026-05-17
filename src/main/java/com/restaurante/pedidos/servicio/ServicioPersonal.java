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

	public Personal crear(CodigoRol codigoRol, String nombreCompleto, String telefono, String correoElectronico, String nombreUsuario, boolean activo) {
		return executeInTransaction(entityManager -> {
			Rol rol = getOrCreateRol(entityManager, codigoRol);

			Personal personal = Personal.builder()
				.rol(rol)
				.nombreCompleto(nombreCompleto)
				.telefono(telefono)
				.correoElectronico(correoElectronico)
				.nombreUsuario(nombreUsuario)
				.activo(activo)
				.creadoEn(Instant.now())
				.build();

			return new RepositorioPersonal(entityManager).save(personal);
		});
	}

	public Personal modificar(Long personalId, CodigoRol codigoRol, String nombreCompleto, String telefono, String correoElectronico, String nombreUsuario, boolean activo) {
		return executeInTransaction(entityManager -> {
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(entityManager);
			Personal personal = repositorioPersonal.findByIdWithRol(personalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + personalId));
			Rol rol = getOrCreateRol(entityManager, codigoRol);

			personal.setRol(rol);
			personal.setNombreCompleto(nombreCompleto);
			personal.setTelefono(telefono);
			personal.setCorreoElectronico(correoElectronico);
			personal.setNombreUsuario(nombreUsuario);
			personal.setActivo(activo);

			return repositorioPersonal.update(personal);
		});
	}

	public void establecerActivo(Long personalId, boolean activo) {
		executeInTransaction(entityManager -> {
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(entityManager);
			Personal personal = repositorioPersonal.findByIdWithRol(personalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + personalId));

			personal.setActivo(activo);
			repositorioPersonal.update(personal);
			return null;
		});
	}

	public void eliminar(Long personalId) {
		executeInTransaction(entityManager -> {
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(entityManager);
			Personal personal = repositorioPersonal.findByIdWithRol(personalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + personalId));

			repositorioPersonal.delete(personal);
			return null;
		});
	}

	private Rol getOrCreateRol(EntityManager entityManager, CodigoRol codigoRol) {
		RepositorioRol repositorioRol = new RepositorioRol(entityManager);

		return repositorioRol.findByCode(codigoRol)
			.orElseGet(() -> repositorioRol.save(Rol.builder().codigoRol(codigoRol).build()));
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
