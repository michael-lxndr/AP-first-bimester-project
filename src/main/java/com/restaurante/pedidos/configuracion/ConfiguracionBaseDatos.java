package com.restaurante.pedidos.configuracion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class ConfiguracionBaseDatos {
	private static EntityManagerFactory entityManagerFactory;

	private ConfiguracionBaseDatos() {
	}

	public static EntityManager createEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	private static synchronized EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("firstBimesterPU");
		}

		return entityManagerFactory;
	}

	public static synchronized void close() {
		if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
	}
}
