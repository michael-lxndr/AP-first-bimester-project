package com.restaurante.pedidos.configuracion;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracionBaseDatosTest {
	@Test
	void debeExponerFabricaDeEntityManager() {
		EntityManagerFactory factory = ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades();
		assertNotNull(factory);
	}

	@Test
	void jpaXmlDeIdeaUsaUnidadPrimerBimestre() throws Exception {
		String jpaXml = Files.readString(Path.of(".idea", "jpa.xml"));
		assertTrue(jpaXml.contains("primerBimestrePU"));
	}

	@Test
	void aplicacionPrincipalApuntaAJavaFx() {
		assertNotNull(com.restaurante.pedidos.presentacion.AplicacionPrincipal.class);
		assertNotNull(com.restaurante.pedidos.presentacion.AplicacionJavaFx.class);
	}
}
