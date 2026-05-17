package com.restaurante.pedidos.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class DatabaseConfig {
	private static EntityManagerFactory entityManagerFactory;

	private DatabaseConfig() {
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
