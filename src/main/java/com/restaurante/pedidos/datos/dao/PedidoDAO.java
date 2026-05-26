package com.restaurante.pedidos.datos.dao;

import com.restaurante.pedidos.Clases.PedidosCliente;
import com.restaurante.pedidos.Logica.PedidosClienteJpaController;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Data Access Object (DAO) para PedidosCliente.
 * Encapsula el acceso a la persistencia (EclipseLink JPA) y respeta la capa de datos actual.
 */
public class PedidoDAO {
    private final PedidosClienteJpaController controller;

    public PedidoDAO() {
        this.controller = new PedidosClienteJpaController(JPABaseDeDatos.getEntityManagerFactory());
    }

    public void crearPedido(PedidosCliente pedido) throws Exception {
        controller.create(pedido);
    }

    public void actualizar(PedidosCliente pedido) throws Exception {
        controller.edit(pedido);
    }

    public PedidosCliente buscarPorId(Long id) {
        EntityManager em = JPABaseDeDatos.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(PedidosCliente.class, id);
        } finally {
            em.close();
        }
    }

    public PedidosCliente buscarPorCodigo(String codigo) {
        return controller.findByCodigo(codigo);
    }

    public List<PedidosCliente> buscarTodos() {
        return controller.findPedidosClienteEntities();
    }
}
