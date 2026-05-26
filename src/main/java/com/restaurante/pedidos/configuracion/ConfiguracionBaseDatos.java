package com.restaurante.pedidos.configuracion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;

/**
 * Clase de soporte para alinear la configuración de persistencia del proyecto
 * con la suite de pruebas. Centraliza el acceso al EntityManagerFactory de javax.
 */
public class ConfiguracionBaseDatos {

    public static EntityManagerFactory obtenerFabricaAdministradorDeEntidades() {
        return JPABaseDeDatos.getEntityManagerFactory();
    }

    public static EntityManager crearAdministradorDeEntidad() {
        return JPABaseDeDatos.getEntityManagerFactory().createEntityManager();
    }

    public static void cerrar() {
        if (JPABaseDeDatos.getEntityManagerFactory().isOpen()) {
            // Se puede cerrar en caso de requerirse, pero usualmente se mantiene abierto.
        }
    }
}
