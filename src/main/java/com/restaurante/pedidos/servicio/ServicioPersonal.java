package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.Personal;
import com.restaurante.pedidos.dominio.entidad.Rol;
import com.restaurante.pedidos.repositorio.RepositorioPersonal;
import com.restaurante.pedidos.repositorio.RepositorioRol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.Instant;
import java.util.List;

public class ServicioPersonal {
	public List<Personal> buscarTodos() {
		try (EntityManager entityManager = ConfiguracionBaseDatos.crearAdministradorDeEntidad()) {
			return new RepositorioPersonal(entityManager).buscarPorRol();
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

			return new RepositorioPersonal(entityManager).guardar(personal);
		});
	}

	public Personal modificar(Long personalId, CodigoRol codigoRol, String nombreCompleto, String telefono, String correoElectronico, String nombreUsuario, boolean activo) {
		return executeInTransaction(entityManager -> {
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(entityManager);
			Personal personal = repositorioPersonal.buscarPorIdConRol(personalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + personalId));
			Rol rol = getOrCreateRol(entityManager, codigoRol);

			personal.setRol(rol);
			personal.setNombreCompleto(nombreCompleto);
			personal.setTelefono(telefono);
			personal.setCorreoElectronico(correoElectronico);
			personal.setNombreUsuario(nombreUsuario);
			personal.setActivo(activo);

			return repositorioPersonal.actualizar(personal);
		});
	}

	public void establecerActivo(Long personalId, boolean activo) {
		executeInTransaction(entityManager -> {
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(entityManager);
			Personal personal = repositorioPersonal.buscarPorIdConRol(personalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + personalId));

			personal.setActivo(activo);
			repositorioPersonal.actualizar(personal);
			return null;
		});
	}

	public void eliminar(Long personalId) {
		executeInTransaction(entityManager -> {
			RepositorioPersonal repositorioPersonal = new RepositorioPersonal(entityManager);
			Personal personal = repositorioPersonal.buscarPorIdConRol(personalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe personal con ID " + personalId));

			repositorioPersonal.borrar(personal);
			return null;
		});
	}

	private Rol getOrCreateRol(EntityManager entityManager, CodigoRol codigoRol) {
		RepositorioRol repositorioRol = new RepositorioRol(entityManager);

		return repositorioRol.buscarPorCodigo(codigoRol)
			.orElseGet(() -> repositorioRol.guardar(Rol.builder().codigoRol(codigoRol).build()));
	}

	private <T> T executeInTransaction(TransactionWork<T> work) {
		try (EntityManager entityManager = ConfiguracionBaseDatos.crearAdministradorDeEntidad()) {
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
