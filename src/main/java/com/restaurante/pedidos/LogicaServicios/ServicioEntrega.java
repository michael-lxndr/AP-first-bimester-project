/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import Clases.EstadosPedido;
import Clases.PedidosCliente;
import Logica.EstadosPedidoJpaController;
import Logica.PedidosClienteJpaController;
import Logica.exceptions.NonexistentEntityException;
import LogicaConfiguracion.JPABaseDeDatos;

/**
 *
 * @author Javier Montaño
 */
public class ServicioEntrega {

    private MaquinaEstadosPedido maquina;
    private PedidosClienteJpaController pedidosClienteControlador;
    private EstadosPedidoJpaController estadosPedidoControlador;

    public ServicioEntrega() {

        maquina = new MaquinaEstadosPedido();

        pedidosClienteControlador = new PedidosClienteJpaController(JPABaseDeDatos.getEntityManagerFactory());
        estadosPedidoControlador = new EstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());
    }

    public void iniciarEntrega(PedidosCliente pedido) throws NonexistentEntityException, Exception {

        EstadosPedido estadoEnCamino = estadosPedidoControlador.findEstadosPedido(4L);

        maquina.cambiarEstado(pedido, estadoEnCamino);

        pedidosClienteControlador.edit(pedido);
    }

    public void confirmarEntrega(PedidosCliente pedido) throws NonexistentEntityException, Exception {

        EstadosPedido estadoEntregado = estadosPedidoControlador.findEstadosPedido(5L);
        maquina.cambiarEstado(pedido, estadoEntregado);

        pedidosClienteControlador.edit(pedido);
    }
}
