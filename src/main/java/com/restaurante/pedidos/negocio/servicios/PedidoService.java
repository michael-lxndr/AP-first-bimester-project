package com.restaurante.pedidos.negocio.servicios;

import com.restaurante.pedidos.Clases.*;
import com.restaurante.pedidos.Clases.Enums.CodigoEstadoPedido;
import com.restaurante.pedidos.datos.dao.PedidoDAO;
import com.restaurante.pedidos.Logica.EstadosPedidoJpaController;
import com.restaurante.pedidos.Logica.HistorialEstadosPedidoJpaController;
import com.restaurante.pedidos.Logica.PersonalJpaController;
import com.restaurante.pedidos.Logica.EntregasJpaController;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;
import com.restaurante.pedidos.LogicaServicios.MaquinaEstadosPedido;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Servicio de negocio para coordinar validación de reglas de transición de pedidos,
 * persistencia y auditoría de historial en base de datos.
 */
public class PedidoService {
    private final PedidoDAO pedidoDAO;
    private final EstadosPedidoJpaController estadosController;
    private final HistorialEstadosPedidoJpaController historialController;
    private final PersonalJpaController personalController;
    private final EntregasJpaController entregasController;
    private final MaquinaEstadosPedido maquinaEstados;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAO();
        this.estadosController = new EstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());
        this.historialController = new HistorialEstadosPedidoJpaController(JPABaseDeDatos.getEntityManagerFactory());
        this.personalController = new PersonalJpaController(JPABaseDeDatos.getEntityManagerFactory());
        
        EntregasJpaController tempController = null;
        try {
            tempController = new EntregasJpaController(JPABaseDeDatos.getEntityManagerFactory());
        } catch (Exception e) {}
        this.entregasController = tempController;
        
        this.maquinaEstados = new MaquinaEstadosPedido();
    }

    /**
     * Registra un pedido en base de datos y añade el primer historial.
     */
    public synchronized void crearPedido(PedidosCliente pedido) throws Exception {
        EstadosPedido estadoInicial = getEstadoPorCodigo("PENDIENTE");
        pedido.setEstadoActualId(estadoInicial);
        pedido.setCreadoEn(new Date());
        pedido.setEstadoActualCambiadoEn(new Date());
        
        pedidoDAO.crearPedido(pedido);

        // Registrar historial inicial
        guardarHistorial(pedido, null, estadoInicial, "Pedido registrado", pedido.getRegistradoPorPersonalId());
    }

    /**
     * Actualiza el estado de un pedido y añade una auditoría al historial.
     * Reglas de transición son validadas por la máquina de estados.
     */
    public synchronized boolean actualizarEstado(Long pedidoId, String nuevoEstadoStr, Long personalId) throws Exception {
        PedidosCliente pedido = pedidoDAO.buscarPorId(pedidoId);
        if (pedido == null) return false;

        EstadosPedido estadoOrigen = pedido.getEstadoActualId();
        EstadosPedido estadoDestino = getEstadoPorCodigo(nuevoEstadoStr);
        if (estadoDestino == null) return false;

        // Validar transición
        CodigoEstadoPedido actualEnum = CodigoEstadoPedido.valueOf(estadoOrigen.getCodigoEstado());
        CodigoEstadoPedido nuevoEnum = CodigoEstadoPedido.valueOf(estadoDestino.getCodigoEstado());

        if (!maquinaEstados.validarCambioEstado(actualEnum, nuevoEnum)) {
            throw new IllegalStateException("Transición inválida de " + actualEnum + " a " + nuevoEnum);
        }

        Personal personal = null;
        if (personalId != null) {
            personal = personalController.findPersonal(personalId);
        }

        // Registrar despacho o entrega en la tabla de entregas
        if (nuevoEstadoStr.equalsIgnoreCase("EN_CAMINO")) {
            registrarDespacho(pedido, personal);
        } else if (nuevoEstadoStr.equalsIgnoreCase("ENTREGADO")) {
            registrarEntregaExitosa(pedido, personal);
        }

        // Actualizar entidad
        pedido.setEstadoActualId(estadoDestino);
        pedido.setEstadoActualCambiadoEn(new Date());
        pedidoDAO.actualizar(pedido);

        // Guardar historial
        guardarHistorial(pedido, estadoOrigen, estadoDestino, "Cambio de estado a " + nuevoEstadoStr, personal);
        return true;
    }

    public PedidosCliente buscarPedidoPorCodigo(String codigo) {
        return pedidoDAO.buscarPorCodigo(codigo);
    }

    public PedidosCliente buscarPedidoPorId(Long id) {
        return pedidoDAO.buscarPorId(id);
    }

    public List<PedidosCliente> buscarTodos() {
        return pedidoDAO.buscarTodos();
    }

    private EstadosPedido getEstadoPorCodigo(String codigo) {
        try {
            for (EstadosPedido ep : estadosController.findEstadosPedidoEntities()) {
                if (ep.getCodigoEstado().equalsIgnoreCase(codigo)) {
                    return ep;
                }
            }
        } catch (Exception e) {
            // Ignorar para fallback autónomo
        }
        return new EstadosPedido(1L, codigo, codigo, 1, false);
    }

    private void registrarDespacho(PedidosCliente pedido, Personal personal) {
        if (entregasController == null) return;
        try {
            Entregas e = pedido.getEntregas();
            if (e == null) {
                e = new Entregas();
                e.setPedidoId(pedido);
                e.setRepartidorPersonalId(personal != null ? personal : getRepartidorFallback());
                e.setDespachadoEn(new Date());
                e.setEstadoEntrega("EN_CAMINO");
                e.setNumeroIntento(1);
                
                entregasController.create(e);
                pedido.setEntregas(e);
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Error al registrar despacho en MySQL: " + ex.getMessage());
        }
    }

    private void registrarEntregaExitosa(PedidosCliente pedido, Personal personal) {
        if (entregasController == null) return;
        try {
            Entregas e = pedido.getEntregas();
            if (e == null) {
                e = new Entregas();
                e.setPedidoId(pedido);
                e.setRepartidorPersonalId(personal != null ? personal : getRepartidorFallback());
                e.setDespachadoEn(new Date());
                e.setEntregadoEn(new Date());
                e.setNombreReceptor(pedido.getClienteId() != null ? pedido.getClienteId().getNombreCompleto() : "Cliente");
                e.setEstadoEntrega("ENTREGADO");
                e.setNumeroIntento(1);
                
                entregasController.create(e);
                pedido.setEntregas(e);
            } else {
                e.setEntregadoEn(new Date());
                e.setNombreReceptor(pedido.getClienteId() != null ? pedido.getClienteId().getNombreCompleto() : "Cliente");
                e.setEstadoEntrega("ENTREGADO");
                if (personal != null) {
                    e.setRepartidorPersonalId(personal);
                }
                entregasController.edit(e);
            }
        } catch (Exception ex) {
            System.out.println("⚠️ Error al registrar entrega en MySQL: " + ex.getMessage());
        }
    }

    private Personal getRepartidorFallback() {
        try {
            for (Personal p : personalController.findPersonalEntities()) {
                if (p.getRolId() != null && p.getRolId().getRolId() == 3L) {
                    return p;
                }
            }
            List<Personal> pList = personalController.findPersonalEntities();
            if (!pList.isEmpty()) return pList.get(0);
        } catch (Exception e) {}
        Personal fallback = new Personal(3L);
        fallback.setNombreCompleto("Repartidor Autónomo");
        return fallback;
    }

    private void guardarHistorial(PedidosCliente pedido, EstadosPedido origen, EstadosPedido destino, String notas, Personal personal) {
        try {
            HistorialEstadosPedido h = new HistorialEstadosPedido();
            h.setPedidoId(pedido);
            h.setEstadoOrigenId(origen != null ? origen : destino);
            h.setEstadoDestinoId(destino);
            h.setCambiadoEn(new Date());
            h.setNotas(notesTruncate(notas));
            h.setCambiadoPorPersonalId(personal != null ? personal : getPersonalFallback());
            historialController.create(h);
        } catch (Exception e) {
            // Fallback silencioso si BD está offline
        }
    }

    private String notesTruncate(String str) {
        if (str == null) return "";
        return str.length() > 255 ? str.substring(0, 250) : str;
    }

    private Personal getPersonalFallback() {
        try {
            List<Personal> p = personalController.findPersonalEntities();
            if (!p.isEmpty()) return p.get(0);
        } catch (Exception e) {}
        Personal fallback = new Personal(1L);
        fallback.setNombreCompleto("Sistema");
        return fallback;
    }
}
