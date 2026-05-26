package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.LogicaServicios.ServicioPedido;
import com.restaurante.pedidos.LogicaServicios.ServicioCocina;
import com.restaurante.pedidos.LogicaServicios.ServicioEntrega;
import com.restaurante.pedidos.LogicaServicios.ServicioSimulacion;
import com.restaurante.pedidos.LogicaServicios.ServicioSimulacionConcurrente;

/**
 * Fachada central que expone los servicios de negocio a la capa de presentación y pruebas,
 * aislando los controladores JavaFX y la consola de la persistencia directa.
 */
public class FachadaServiciosPedido {

    private final ServicioPedido pedido;
    private final ServicioCocina cocina;
    private final ServicioEntrega entrega;
    private final ServicioSimulacion simulacion;
    private final ServicioSimulacionConcurrente simulacionConcurrente;

    public FachadaServiciosPedido() {
        this.pedido = new ServicioPedido();
        this.cocina = new ServicioCocina();
        this.entrega = new ServicioEntrega();
        this.simulacion = new ServicioSimulacion(this.pedido);
        this.simulacionConcurrente = new ServicioSimulacionConcurrente();
    }

    public ServicioPedido pedido() {
        return pedido;
    }

    public ServicioCocina cocina() {
        return cocina;
    }

    public ServicioEntrega entrega() {
        return entrega;
    }

    public ServicioSimulacion simulacion() {
        return simulacion;
    }

    public com.restaurante.pedidos.LogicaServicios.ServicioSimulacionConcurrente simulacionConcurrente() {
        return simulacionConcurrente;
    }
}
