package com.restaurante.pedidos.servicio;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.logica.Utilidades.GeneradorCodigoPedido;
import com.restaurante.pedidos.clases.*;
import com.restaurante.pedidos.logica.PersonalJpaController;
import com.restaurante.pedidos.LogicaServicios.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requiere base de datos MySQL activa en localhost:3306")
class ServicioEscenariosIntegracionTest {

    @Test
    void creaPedidoConEstadoPendienteYConsultaPorCodigo() throws Exception {
        Fixture fixture = new Fixture();
        PedidosCliente pedido = fixture.nuevoPedidoBase();

        ServicioPedido servicioPedido = new ServicioPedido();
        servicioPedido.crearPedido(pedido);

        assertNotNull(pedido.getPedidoId());
        assertEquals("PENDIENTE", pedido.getEstadoActualId().getCodigoEstado());

        PedidosCliente buscado = servicioPedido.buscarPedidoPorCodigo(pedido.getCodigoPedido());
        assertNotNull(buscado);
        assertEquals(pedido.getCodigoPedido(), buscado.getCodigoPedido());
        assertEquals("PENDIENTE", servicioPedido.consultarEstadoPorCodigo(pedido.getCodigoPedido()));
    }

    @Test
    void asignaRepartidorElegibleYAvanzaAEnCamino() throws Exception {
        Fixture fixture = new Fixture();
        PedidosCliente pedidoListo = fixture.nuevoPedidoBase();

        ServicioPedido servicioPedido = new ServicioPedido();
        servicioPedido.crearPedido(pedidoListo);

        ServicioCocina servicioCocina = new ServicioCocina();
        servicioCocina.iniciarPreparacion(pedidoListo);
        servicioCocina.marcarListo(pedidoListo);

        PersonalJpaController personalController = new PersonalJpaController(ConfiguracionBaseDatos.obtenerFabricaAdministradorDeEntidades());
        List<Personal> disponibles = personalController.findPersonalEntities(); // Repartidores disponibles simplificado
        assertFalse(disponibles.isEmpty());

        ServicioEntrega servicioEntrega = new ServicioEntrega();
        servicioEntrega.iniciarEntrega(pedidoListo);
        assertNotNull(pedidoListo.getPedidoId());
        assertEquals("EN_CAMINO", servicioPedido.consultarEstadoPorCodigo(pedidoListo.getCodigoPedido()));
    }

    @Test
    void simulaPedidoHastaEntregadoConRutaCompleta() throws Exception {
        Fixture fixture = new Fixture();
        PedidosCliente pedido = fixture.nuevoPedidoBase();
        ServicioPedido servicioPedido = new ServicioPedido();
        servicioPedido.crearPedido(pedido);

        ServicioSimulacion simulacion = new ServicioSimulacion(servicioPedido);
        List<String> historial = simulacion.simularPedido(pedido);

        assertNotNull(historial);
        assertEquals("ENTREGADO", servicioPedido.consultarEstadoPorCodigo(pedido.getCodigoPedido()));
    }

    private static final class Fixture {
        private final Clientes cliente;
        private final DireccionesCliente direccion;
        private final Personal admin;

        private Fixture() {
            EntityManager em = ConfiguracionBaseDatos.crearAdministradorDeEntidad();
            try {
                var tx = em.getTransaction();
                tx.begin();
                Roles adminRol = asegurarRol(em, CodigoRol.ADMINISTRADOR);
                asegurarRol(em, CodigoRol.COCINERO);
                asegurarRol(em, CodigoRol.REPARTIDOR);
                asegurarEstadosYReglas(em);

                this.cliente = new Clientes();
                cliente.setNombreCompleto("Cliente Escenario " + System.nanoTime());
                cliente.setTelefono("0991234567");
                cliente.setCorreoElectronico("escenario@test.com");
                cliente.setActivo(true);
                cliente.setCreadoEn(new Date());
                em.persist(cliente);

                this.direccion = new DireccionesCliente();
                direccion.setClienteId(cliente);
                direccion.setAlias("Casa");
                direccion.setCallePrincipal("Av. Principal");
                direccion.setCiudad("Quito");
                direccion.setProvincia("Pichincha");
                direccion.setPais("Ecuador");
                direccion.setPrincipal(true);
                direccion.setActiva(true);
                em.persist(direccion);

                this.admin = new Personal();
                admin.setRolId(adminRol);
                admin.setNombreCompleto("Admin Escenario");
                admin.setCorreoElectronico("admin.escenario@test.com");
                admin.setNombreUsuario("admin_escenario_" + System.nanoTime());
                admin.setActivo(true);
                admin.setCreadoEn(new Date());
                em.persist(admin);

                asegurarRepartidorDisponible(em);
                tx.commit();
            } finally {
                em.close();
            }
        }

        private PedidosCliente nuevoPedidoBase() {
            PedidosCliente pedido = new PedidosCliente();
            pedido.setCodigoPedido(GeneradorCodigoPedido.generar());
            pedido.setClienteId(cliente);
            pedido.setRegistradoPorPersonalId(admin);
            pedido.setDireccionEntregaId(direccion);
            pedido.setSnapshotDireccionEntrega("Av. Principal");
            pedido.setSubtotal(new BigDecimal("10.00"));
            pedido.setImpuesto(new BigDecimal("1.20"));
            pedido.setDescuento(BigDecimal.ZERO);
            pedido.setRecargoDireccion(BigDecimal.ZERO);
            pedido.setTotal(new BigDecimal("11.20"));
            pedido.setPrioritario(false);
            pedido.setCreadoEn(new Date());
            pedido.setEstadoActualCambiadoEn(new Date());
            return pedido;
        }

        private static Roles asegurarRol(EntityManager em, CodigoRol codigoRol) {
            var roles = em.createQuery("SELECT r FROM Roles r WHERE r.codigoRol = :codigo", Roles.class)
                .setParameter("codigo", codigoRol.name())
                .setMaxResults(1)
                .getResultList();
            if (!roles.isEmpty()) return roles.get(0);
            Roles rol = new Roles();
            rol.setCodigoRol(codigoRol.name());
            em.persist(rol);
            return rol;
        }

        private static EstadosPedido asegurarEstado(EntityManager em, CodigoEstadoPedido codigo, int orden, boolean finalizado) {
            var estados = em.createQuery("SELECT e FROM EstadosPedido e WHERE e.codigoEstado = :codigo", EstadosPedido.class)
                .setParameter("codigo", codigo.name())
                .setMaxResults(1)
                .getResultList();
            if (!estados.isEmpty()) return estados.get(0);
            EstadosPedido estado = new EstadosPedido();
            estado.setCodigoEstado(codigo.name());
            estado.setNombreEstado(codigo.name());
            estado.setOrdenEstado(orden);
            estado.setFinalizado(finalizado);
            em.persist(estado);
            return estado;
        }

        private static void asegurarEstadosYReglas(EntityManager em) {
            EstadosPedido pendiente = asegurarEstado(em, CodigoEstadoPedido.PENDIENTE, 1, false);
            EstadosPedido prep = asegurarEstado(em, CodigoEstadoPedido.EN_PREPARACION, 2, false);
            EstadosPedido listo = asegurarEstado(em, CodigoEstadoPedido.LISTO, 3, false);
            EstadosPedido camino = asegurarEstado(em, CodigoEstadoPedido.EN_CAMINO, 4, false);
            EstadosPedido entregado = asegurarEstado(em, CodigoEstadoPedido.ENTREGADO, 5, true);
            Roles cocinero = asegurarRol(em, CodigoRol.COCINERO);
            Roles repartidor = asegurarRol(em, CodigoRol.REPARTIDOR);
            asegurarRegla(em, pendiente, prep, cocinero);
            asegurarRegla(em, prep, listo, cocinero);
            asegurarRegla(em, listo, camino, repartidor);
            asegurarRegla(em, camino, entregado, repartidor);
        }

        private static void asegurarRegla(EntityManager em, EstadosPedido origen, EstadosPedido destino, Roles rol) {
            Long count = em.createQuery("""
                SELECT COUNT(r) FROM ReglasTransicionEstadoPedido r
                WHERE r.estadoOrigenId = :origen AND r.estadoDestinoId = :destino AND r.rolId = :rol
            """, Long.class).setParameter("origen", origen).setParameter("destino", destino).setParameter("rol", rol).getSingleResult();
            if (count > 0) return;
            ReglasTransicionEstadoPedido regla = new ReglasTransicionEstadoPedido();
            regla.setEstadoOrigenId(origen);
            regla.setEstadoDestinoId(destino);
            regla.setRolId(rol);
            regla.setActiva(true);
            em.persist(regla);
        }

        private static void asegurarRepartidorDisponible(EntityManager em) {
            Roles rol = asegurarRol(em, CodigoRol.REPARTIDOR);
            Personal repartidor = new Personal();
            repartidor.setRolId(rol);
            repartidor.setNombreCompleto("Repartidor Disponible " + System.nanoTime());
            repartidor.setCorreoElectronico("repartidor.disponible@test.com");
            repartidor.setNombreUsuario("rep_disp_" + System.nanoTime());
            repartidor.setActivo(true);
            repartidor.setCreadoEn(new Date());
            em.persist(repartidor);
        }
    }
}
