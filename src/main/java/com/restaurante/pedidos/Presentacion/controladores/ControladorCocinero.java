package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;
import com.restaurante.pedidos.presentacion.componentes.TarjetaPedido;
import com.restaurante.pedidos.presentacion.util.EjecutorUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista del cocinero.
 *
 * Reglas aplicadas:
 * ✅ No usa EntityManager ni repositorios.
 * ✅ No crea hilos directamente (usa EjecutorUI).
 * ✅ Solo conoce DTOs, no entidades JPA.
 * ✅ Lógica de estado delegada al servicio.
 */
public class ControladorCocinero {

    @FXML private VBox contenedorCola;
    @FXML private Label lblEstadoConexion;
    @FXML private Button btnRefrescar;

    // ✅ Inyección de dependencia: interfaz, no implementación
    private IServicioPedidosUI servicioPedidos;

    // Para tests: setter de inyección
    public void setServicioPedidos(IServicioPedidosUI servicio) {
        this.servicioPedidos = servicio;
    }

    @FXML
    public void initialize() {
        // ✅ Inicialización segura: servicio puede ser null en diseño FXML
        if (servicioPedidos == null) {
            System.err.println("⚠️  ServicioPedidos no inyectado en ControladorCocinero");
            lblEstadoConexion.setText("❌ Servicio no disponible");
            return;
        }

        lblEstadoConexion.setText("🔄 Cargando...");
        cargarColaPreparacion();

        // Auto-refresco cada 30 segundos (opcional, para UI en tiempo real)
        // Usamos EjecutorUI para no bloquear el thread de JavaFX
        EjecutorUI.ejecutarCpu(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(30_000);
                    Platform.runLater(this::cargarColaPreparacion);
                }
            } catch (InterruptedException e) {
                // Shutdown limpio
                Thread.currentThread().interrupt();
            }
        });
    }

    @FXML
    private void cargarColaPreparacion() {
        lblEstadoConexion.setText("🔄 Actualizando...");
        btnRefrescar.setDisable(true);

        // ✅ Patrón: EjecutorUI + callback para UI
        EjecutorUI.ejecutarConCallbackUI(
                () -> servicioPedidos.obtenerPedidosPorEstado(CodigoEstadoPedido.EN_PREPARACION).join(),

                // onSuccess: se ejecuta en thread de JavaFX (Platform.runLater interno)
                pedidos -> {
                    contenedorCola.getChildren().clear();

                    if (pedidos.isEmpty()) {
                        contenedorCola.getChildren().add(
                                new Label("🎉 No hay pedidos en preparación")
                        );
                        lblEstadoConexion.setText("✅ Sin pedidos pendientes");
                    } else {
                        for (PedidoDTO pedido : pedidos) {
                            TarjetaPedido tarjeta = new TarjetaPedido(
                                    pedido,
                                    this::marcarComoListo,  // Callback
                                    "✅ Listo",
                                    "estado-en-preparacion"
                            );
                            contenedorCola.getChildren().add(tarjeta);
                        }
                        lblEstadoConexion.setText("✅ " + pedidos.size() + " pedidos en cola");
                    }
                    btnRefrescar.setDisable(false);
                },

                // onError: manejo centrado en usuario
                error -> {
                    System.err.println("Error cargando cola: " + error.getMessage());
                    lblEstadoConexion.setText("❌ Error de conexión");
                    btnRefrescar.setDisable(false);

                    // Mostrar alerta no intrusiva
                    mostrarNotificacion("No se pudo actualizar la cola. Reintente.", Alert.AlertType.WARNING);
                }
        );
    }

    @FXML
    private void marcarComoListo(PedidoDTO pedido) {
        // Confirmación antes de acción destructiva
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar acción");
        confirmacion.setHeaderText("Marcar pedido como listo");
        confirmacion.setContentText("¿Listo para entregar \"" + pedido.codigoPedido() + "\"?\n\nEsto notificará al repartidor.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;  // Usuario canceló
        }

        // ✅ Delegar lógica de negocio al servicio
        EjecutorUI.ejecutarConCallbackUI(
                () -> servicioPedidos.transicionarEstado(
                        pedido.id(),
                        CodigoEstadoPedido.LISTO,
                        obtenerIdPersonalActual()  // Debería venir de sesión autenticada
                ).join(),

                exito -> {
                    if (exito) {
                        mostrarNotificacion("🎉 Pedido " + pedido.codigoPedido() + " marcado como listo", Alert.AlertType.INFORMATION);
                        cargarColaPreparacion();  // Refrescar UI
                    } else {
                        mostrarNotificacion("⚠️  No se pudo actualizar el estado. Verifique permisos.", Alert.AlertType.WARNING);
                    }
                },

                error -> {
                    System.err.println("Error transicionando estado: " + error.getMessage());
                    mostrarNotificacion("❌ Error de comunicación con el servidor", Alert.AlertType.ERROR);
                }
        );
    }

    // === Métodos auxiliares ===

    private Long obtenerIdPersonalActual() {
        // TODO: Integrar con sistema de autenticación
        // Por ahora, hardcodeado para desarrollo
        return 2L;  // Ana, la cocinera
    }

    private void mostrarNotificacion(String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle("Notificación");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.initOwner(btnRefrescar.getScene().getWindow());
            alert.showAndWait();
        });
    }

    // Para tests: exponer estado interno
    public int getCantidadTarjetasEnCola() {
        return (int) contenedorCola.getChildren().stream()
                .filter(n -> n instanceof TarjetaPedido)
                .count();
    }
}