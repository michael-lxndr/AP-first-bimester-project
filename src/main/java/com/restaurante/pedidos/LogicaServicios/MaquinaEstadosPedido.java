/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.Clases.Enums.CodigoEstadoPedido;
import com.restaurante.pedidos.Clases.EstadosPedido;
import com.restaurante.pedidos.Clases.PedidosCliente;


public class MaquinaEstadosPedido {

    public boolean validarCambioEstado(CodigoEstadoPedido actual, CodigoEstadoPedido nuevo){

        switch (actual){

            case PENDIENTE:
                return nuevo == CodigoEstadoPedido.EN_PREPARACION;

            case EN_PREPARACION:
                return nuevo == CodigoEstadoPedido.LISTO;

            case LISTO:
                return nuevo == CodigoEstadoPedido.EN_CAMINO;

            case EN_CAMINO:
                return nuevo == CodigoEstadoPedido.ENTREGADO;

            default:
                return false;
        }
    }

    public void cambiarEstado(PedidosCliente pedido, EstadosPedido nuevoEstadoEntidad){

        CodigoEstadoPedido actual = CodigoEstadoPedido.valueOf(pedido.getEstadoActualId().getCodigoEstado());

        CodigoEstadoPedido nuevo = CodigoEstadoPedido.valueOf(nuevoEstadoEntidad.getCodigoEstado());

        boolean valido = validarCambioEstado(actual, nuevo);

        if (!valido) {
            throw new IllegalStateException("Transaccion de estado inválida");
        }

        pedido.setEstadoActualId(nuevoEstadoEntidad);
    }
}
