package com.restaurante.pedidos.servicio;

import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FachadaServiciosPedidoTest {

    @BeforeEach
    void setUp() {
        EntityManagerFactory mockFactory = Mockito.mock(EntityManagerFactory.class);
        JPABaseDeDatos.setEntityManagerFactory(mockFactory);
    }

    @Test
    void exponeServiciosDePedidoCocinaEntregaYSimulacion() {
        FachadaServiciosPedido fachada = new FachadaServiciosPedido();
        assertNotNull(fachada.pedido());
        assertNotNull(fachada.cocina());
        assertNotNull(fachada.entrega());
        assertNotNull(fachada.simulacion());
    }
}
