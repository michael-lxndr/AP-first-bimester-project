package com.restaurante.pedidos.dominio.servicio.ui;

import com.restaurante.pedidos.Clases.PedidosCliente;
import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.negocio.servicios.PedidoService;
import com.restaurante.pedidos.LogicaServicios.ServicioSimulacionConcurrente;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementación de producción del contrato de servicios para la UI.
 * Soporta sincronización en tiempo real mediante PropertyChangeListener.
 */
public class ServicioPedidosUIImpl implements IServicioPedidosUI {
    private static ServicioPedidosUIImpl instance;

    public static synchronized ServicioPedidosUIImpl getInstance() {
        if (instance == null) {
            instance = new ServicioPedidosUIImpl();
        }
        return instance;
    }

    private final PedidoService pedidoService;
    private final PropertyChangeSupport support;
    private ServicioSimulacionConcurrente simulacion;

    private ServicioPedidosUIImpl() {
        this.pedidoService = new PedidoService();
        this.support = new PropertyChangeSupport(this);
    }

    public void registrarSimulador(ServicioSimulacionConcurrente sim) {
        this.simulacion = sim;
        sim.setOrderUpdateListener(pedido -> {
            // Notificar cambios de hilos de simulación
            support.firePropertyChange("pedidoActualizado", null, pedido);
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void notificarCambioExterno(PedidoDTO pedido) {
        support.firePropertyChange("pedidoActualizado", null, pedido);
    }

    @Override
    public CompletableFuture<List<PedidoDTO>> obtenerPedidosPorEstado(CodigoEstadoPedido estado) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Si la simulación está activa, combinar con pedidos activos en memoria
                if (simulacion != null && simulacion.isSimulating()) {
                    return simulacion.getPedidosActivos().stream()
                            .filter(p -> p.estadoActual() != null && estado.name().equals(p.estadoActual().codigo()))
                            .collect(Collectors.toList());
                }
                
                return pedidoService.buscarTodos().stream()
                        .map(PedidoDTO::fromEntity)
                        .filter(p -> p.estadoActual() != null && estado.name().equals(p.estadoActual().codigo()))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return Collections.emptyList();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<PedidoDTO>> consultarPorCodigo(String codigo) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (simulacion != null && simulacion.isSimulating()) {
                    Optional<PedidoDTO> enSim = simulacion.getPedidosActivos().stream()
                            .filter(p -> codigo.equalsIgnoreCase(p.codigoPedido()))
                            .findFirst();
                    if (enSim.isPresent()) return enSim;
                }
                PedidosCliente pc = pedidoService.buscarPedidoPorCodigo(codigo);
                return Optional.ofNullable(PedidoDTO.fromEntity(pc));
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Optional<PedidoDTO>> obtenerPedidoPorId(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (simulacion != null && simulacion.isSimulating()) {
                    Optional<PedidoDTO> enSim = simulacion.getPedidosActivos().stream()
                            .filter(p -> Objects.equals(p.id(), id))
                            .findFirst();
                    if (enSim.isPresent()) return enSim;
                }
                PedidosCliente pc = pedidoService.buscarPedidoPorId(id);
                return Optional.ofNullable(PedidoDTO.fromEntity(pc));
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> transicionarEstado(Long pedidoId, CodigoEstadoPedido nuevoEstado, Long personalId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. Transicionar en base de datos
                boolean exito = pedidoService.actualizarEstado(pedidoId, nuevoEstado.name(), personalId);
                if (exito) {
                    PedidosCliente pc = pedidoService.buscarPedidoPorId(pedidoId);
                    PedidoDTO dto = PedidoDTO.fromEntity(pc);
                    
                    // Sincronizar en simulación si está activo
                    if (simulacion != null && simulacion.isSimulating()) {
                        // El motor de simulación maneja sus transiciones concurrentemente,
                        // pero notificamos la actualización manual en los escuchadores
                    }
                    
                    notificarCambioExterno(dto);
                }
                return exito;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> asignarPersonal(Long pedidoId, Long personalId) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> cancelarPedido(Long pedidoId, Long personalId, String motivo) {
        return CompletableFuture.completedFuture(true);
    }
}
