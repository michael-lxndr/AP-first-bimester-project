package com.restaurante.pedidos.configuracion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class ConfiguracionBaseDatos {
	private static EntityManagerFactory laFabricaAdministradorDeEntidades;

	private ConfiguracionBaseDatos() {
	}

	public static EntityManager crearAdministradorDeEntidad() {
		return obtenerFabricaAdministradorDeEntidades().createEntityManager();
	}

	private static synchronized EntityManagerFactory obtenerFabricaAdministradorDeEntidades() {
		if (laFabricaAdministradorDeEntidades == null) {
			laFabricaAdministradorDeEntidades = Persistence.createEntityManagerFactory("primerBimestrePU");
		}

		return laFabricaAdministradorDeEntidades;
	}

	public static synchronized void cerrar() {
		if (laFabricaAdministradorDeEntidades != null && laFabricaAdministradorDeEntidades.isOpen()) {
			laFabricaAdministradorDeEntidades.close();
			laFabricaAdministradorDeEntidades = null;
		}
	}
}
