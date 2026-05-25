package com.restaurante.pedidos.datos.dao;

import com.restaurante.pedidos.Clases.Productos;
import com.restaurante.pedidos.Logica.ProductosJpaController;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Data Access Object (DAO) para Productos.
 */
public class ProductoDAO {
    private final ProductosJpaController controller;

    public ProductoDAO() {
        this.controller = new ProductosJpaController(JPABaseDeDatos.getEntityManagerFactory());
    }

    public List<Productos> buscarTodos() {
        return controller.findProductosEntities();
    }

    public Productos buscarPorId(Long id) {
        EntityManager em = JPABaseDeDatos.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Productos.class, id);
        } finally {
            em.close();
        }
    }
}
