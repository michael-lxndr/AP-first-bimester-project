package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.clases.EstadosPedido;
import com.restaurante.pedidos.clases.ReglasTransicionEstadoPedido;
import com.restaurante.pedidos.clases.Roles;
import com.restaurante.pedidos.LogicaServicios.MaquinaEstadosPedido;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaquinaEstadosPedidoTest {
    @Test
    void validaTransicionConReglaActiva() {
        MaquinaEstadosPedido maquina = new MaquinaEstadosPedido();
        assertTrue(maquina.esTransicionValida(
            CodigoEstadoPedido.PENDIENTE,
            CodigoEstadoPedido.EN_PREPARACION,
            CodigoRol.COCINERO,
            List.of(regla(CodigoRol.COCINERO, CodigoEstadoPedido.PENDIENTE, CodigoEstadoPedido.EN_PREPARACION, true))
        ));
    }

    @Test
    void rechazaTransicionSinReglaActiva() {
        MaquinaEstadosPedido maquina = new MaquinaEstadosPedido();
        assertFalse(maquina.esTransicionValida(
            CodigoEstadoPedido.PENDIENTE,
            CodigoEstadoPedido.EN_CAMINO,
            CodigoRol.REPARTIDOR,
            List.of(regla(CodigoRol.REPARTIDOR, CodigoEstadoPedido.LISTO, CodigoEstadoPedido.EN_CAMINO, true))
        ));
    }

    private ReglasTransicionEstadoPedido regla(CodigoRol rolCodigo, CodigoEstadoPedido origenCodigo, CodigoEstadoPedido destinoCodigo, boolean activa) {
        Roles rol = new Roles();
        rol.setCodigoRol(rolCodigo.name());
        EstadosPedido origen = new EstadosPedido();
        origen.setCodigoEstado(origenCodigo.name());
        EstadosPedido destino = new EstadosPedido();
        destino.setCodigoEstado(destinoCodigo.name());
        ReglasTransicionEstadoPedido regla = new ReglasTransicionEstadoPedido();
        regla.setRolId(rol);
        regla.setEstadoOrigenId(origen);
        regla.setEstadoDestinoId(destino);
        regla.setActiva(activa);
        return regla;
    }
    @Test
    void testTransicionEstadoValida() {
        MaquinaEstadosPedido maquina = new MaquinaEstadosPedido();

        // PENDIENTE -> EN_PREPARACION -> LISTO -> EN_CAMINO -> ENTREGADO
        assertTrue(maquina.validarCambioEstado(com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.PENDIENTE, com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.EN_PREPARACION));
        assertTrue(maquina.validarCambioEstado(com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.EN_PREPARACION, com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.LISTO));
        assertTrue(maquina.validarCambioEstado(com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.LISTO, com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.EN_CAMINO));
        assertTrue(maquina.validarCambioEstado(com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.EN_CAMINO, com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.ENTREGADO));
    }

    @Test
    void testTransicionInvalida() {
        MaquinaEstadosPedido maquina = new MaquinaEstadosPedido();

        // PENDIENTE -> ENTREGADO debe retornar falso
        assertFalse(maquina.validarCambioEstado(com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.PENDIENTE, com.restaurante.pedidos.clases.Enums.CodigoEstadoPedido.ENTREGADO));

        // cambiarEstado con transicion inválida debe lanzar IllegalStateException
        com.restaurante.pedidos.clases.PedidosCliente p = new com.restaurante.pedidos.clases.PedidosCliente();
        com.restaurante.pedidos.clases.EstadosPedido epPendiente = new com.restaurante.pedidos.clases.EstadosPedido(1L, "PENDIENTE", "Pendiente", 1, false);
        p.setEstadoActualId(epPendiente);

        com.restaurante.pedidos.clases.EstadosPedido epEntregado = new com.restaurante.pedidos.clases.EstadosPedido(5L, "ENTREGADO", "Entregado", 5, true);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            maquina.cambiarEstado(p, epEntregado);
        });
    }
}
