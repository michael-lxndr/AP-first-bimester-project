package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.CodigoEstadoPedido;
import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorCocinero {

    @FXML private VBox contenedorCola;
    @FXML private Label lblEstadoConexion;
    @FXML private Button btnRefrescar;

    private IServicioPedidosUI servicioPedidos;
    private Long personalId = 2L; // ID del cocinero actual

    // === Métodos para tests ===
    public void setServicioPedidos(IServicioPedidosUI servicioPedidos) {
        this.servicioPedidos = servicioPedidos;
    }

    @FXML
    public void initialize() {
        if (this.servicioPedidos == null) {
            this.servicioPedidos = com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl.getInstance();
        }
        lblEstadoConexion.setText("✅ Conectado - Sistema funcionando");
        
        // Registrar listener de eventos en tiempo real para cambios de estado
        if (this.servicioPedidos instanceof com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl impl) {
            impl.addPropertyChangeListener(evt -> Platform.runLater(this::cargarColaPreparacion));
        }

        cargarColaPreparacion();
    }

    @FXML
    private void cargarColaPreparacion() {
        lblEstadoConexion.setText("🔄 Cargando pedidos...");
        contenedorCola.getChildren().clear();

        if (servicioPedidos != null) {
            servicioPedidos.obtenerPedidosPorEstado(CodigoEstadoPedido.EN_PREPARACION)
                    .thenAccept(pedidos -> Platform.runLater(() -> {
                        if (pedidos != null && !pedidos.isEmpty()) {
                            for (PedidoDTO pedido : pedidos) {
                                agregarTarjetaPedido(pedido);
                            }
                            lblEstadoConexion.setText("✅ " + pedidos.size() + " pedidos en preparación");
                        } else {
                            mostrarMensajeSinPedidos();
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            lblEstadoConexion.setText("❌ Error al cargar pedidos");
                            mostrarMensajeSinPedidos();
                        });
                        return null;
                    });
        } else {
            mostrarMensajeSinPedidos();
            lblEstadoConexion.setText("✅ Sin pedidos pendientes");
        }

        if (btnRefrescar != null) btnRefrescar.setDisable(false);
    }

    private void mostrarMensajeSinPedidos() {
        Label mensaje = new Label("📋 No hay pedidos en preparación\n\n(Funcionalidad en desarrollo)");
        mensaje.setStyle("-fx-padding: 20; -fx-text-fill: #666;");
        contenedorCola.getChildren().add(mensaje);
    }

    private void agregarTarjetaPedido(PedidoDTO pedido) {
        // Crear una tarjeta visual para el pedido
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label codigo = new Label("📄 " + pedido.codigoPedido());
        codigo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label cliente = new Label("👤 " + pedido.nombreCliente());
        Label items = new Label("🍽️ " + (pedido.items() != null ? pedido.items().size() : 0) + " items");
        Label total = new Label("💰 $" + pedido.total().toPlainString());

        Button btnListo = new Button("✅ Marcar como Listo");
        btnListo.setOnAction(e -> marcarComoListo(pedido));

        tarjeta.getChildren().addAll(codigo, cliente, items, total, btnListo);
        contenedorCola.getChildren().add(tarjeta);
    }

    // Package-private para acceso desde tests
    void marcarComoListo(PedidoDTO pedido) {
        if (servicioPedidos != null) {
            servicioPedidos.transicionarEstado(pedido.id(), CodigoEstadoPedido.LISTO, personalId)
                    .thenAccept(resultado -> Platform.runLater(() -> {
                        if (resultado) {
                            mostrarNotificacion("✅ Pedido " + pedido.codigoPedido() + " marcado como listo");
                            cargarColaPreparacion();
                        } else {
                            mostrarNotificacion("⚠️ No se pudo marcar como listo");
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarNotificacion("❌ Error al procesar"));
                        return null;
                    });
        } else {
            mostrarNotificacion("📦 Demo: Pedido " + pedido.codigoPedido() + " marcado como listo");
            cargarColaPreparacion();
        }
    }

    private void mostrarNotificacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) contenedorCola.getScene().getWindow();
        stage.close();
    }
}