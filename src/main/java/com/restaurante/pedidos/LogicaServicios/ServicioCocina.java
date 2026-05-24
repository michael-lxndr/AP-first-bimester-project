 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import Clases.EstadosPedido;
import Clases.PedidosCliente;
import Logica.EstadosPedidoJpaController;
import Logica.PedidosClienteJpaController;
import LogicaConfiguracion.JPABaseDeDatos;

/**
 *
 * @author Javier Montaño
 */
public class ServicioCocina {

    private MaquinaEstadosPedido maquina;
    private PedidosClienteJpaController pedidosClienteControlador;
    private EstadosPedidoJpaController estadosPedidoControlador;

    public ServicioCocina() {

        maquina = new MaquinaEstadosPedido();
        pedidosClienteControlador = new PedidosClienteJpaController(JPABaseDeDatos.getEntityManagerFactory());

        estadosPedidoControlador = new EstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());

    }

    public void iniciarPreparacion(PedidosCliente pedido) throws Exception{

        EstadosPedido estadoPreparacion = estadosPedidoControlador.findEstadosPedido(2L);

        maquina.cambiarEstado(pedido, estadoPreparacion);

        pedidosClienteControlador.edit(pedido);
    }

    public void marcarListo(PedidosCliente pedido) throws Exception {

        EstadosPedido estadoListo = estadosPedidoControlador.findEstadosPedido(3L);

        maquina.cambiarEstado(pedido, estadoListo);

        pedidosClienteControlador.edit(pedido);
    }
}
