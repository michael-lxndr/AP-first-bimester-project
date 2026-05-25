package com.restaurante.pedidos.negocio.servicios;

import com.restaurante.pedidos.Clases.Productos;
import com.restaurante.pedidos.datos.dao.ProductoDAO;
import java.util.List;

/**
 * Servicio de negocio para gestionar el catálogo de productos del restaurante.
 */
public class ProductoService {
    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    public List<Productos> buscarTodos() {
        return productoDAO.buscarTodos();
    }

    public Productos buscarPorId(Long id) {
        return productoDAO.buscarPorId(id);
    }
}
