package com.restaurante.pedidos.persistencia.controlador;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.Clases.EstadosPedido;
import com.restaurante.pedidos.Clases.Roles;
import com.restaurante.pedidos.Logica.RolesJpaController;
import com.restaurante.pedidos.Logica.EstadosPedidoJpaController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled("Requiere base de datos MySQL activa en localhost:3306")
class ControladoresJpaTest {
    @Test
    void buscanPorCodigoConControladores() {
        EntityManagerFactory emf = ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades();
        RolesJpaController roles = new RolesJpaController(emf);
        EstadosPedidoJpaController estados = new EstadosPedidoJpaController(emf);

        Roles rol = roles.findRolesEntities().stream()
                .filter(r -> r.getCodigoRol().equals(CodigoRol.ADMINISTRADOR.name()))
                .findFirst()
                .orElse(null);
                
        if (rol == null) {
            Roles nuevo = new Roles();
            nuevo.setCodigoRol(CodigoRol.ADMINISTRADOR.name());
            roles.create(nuevo);
            rol = nuevo;
        }

        EstadosPedido estado = estados.findEstadosPedidoEntities().stream()
                .filter(e -> e.getCodigoEstado().equals(CodigoEstadoPedido.PENDIENTE.name()))
                .findFirst()
                .orElse(null);

        if (estado == null) {
            EstadosPedido e = new EstadosPedido();
            e.setCodigoEstado(CodigoEstadoPedido.PENDIENTE.name());
            e.setNombreEstado("Pendiente");
            e.setOrdenEstado(1);
            e.setFinalizado(false);
            estados.create(e);
            estado = e;
        }

        assertNotNull(rol.getRolId());
        assertNotNull(estado.getEstadoId());
        ConfiguracionBaseDatos.cerrar();
    }
}
