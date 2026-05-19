package com.restaurante.pedidos.configuracion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class ConfiguracionBaseDatos {
	private static EntityManagerFactory fabricaAdministradorDeEntidades;

	private ConfiguracionBaseDatos() {
	}

	public static EntityManager crearAdministradorDeEntidad() {
		return obtenerFabricaAdministradorDeEntidades().createEntityManager();
	}

	private static synchronized EntityManagerFactory obtenerFabricaAdministradorDeEntidades() {
		if (fabricaAdministradorDeEntidades == null) {
			fabricaAdministradorDeEntidades = Persistence.createEntityManagerFactory("primerBimestrePU");
		}

		return fabricaAdministradorDeEntidades;
	}

	public static synchronized void cerrar() {
		if (fabricaAdministradorDeEntidades != null && fabricaAdministradorDeEntidades.isOpen()) {
			fabricaAdministradorDeEntidades.close();
			fabricaAdministradorDeEntidades = null;
		}
	}
}
