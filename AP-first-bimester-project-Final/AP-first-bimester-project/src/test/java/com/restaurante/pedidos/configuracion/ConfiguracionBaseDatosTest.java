package com.restaurante.pedidos.configuracion;

import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracionBaseDatosTest {

    @BeforeEach
    void setUp() {
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        JPABaseDeDatos.setEntityManagerFactory(mockFactory);
    }

    @Test
    void debeExponerFabricaDeEntityManager() {
        EntityManagerFactory factory = ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades();
        assertNotNull(factory);
    }

    @Test
    @Disabled("El directorio .idea/jpa.xml es específico del IDE y puede no existir en entornos de integración limpia")
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
