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

public class ControladorRepartidor {

    @FXML private VBox contenedorListos;
    @FXML private VBox contenedorEnCamino;
    @FXML private Label lblEstadoConexion;

    private IServicioPedidosUI servicioPedidos;
    private Long personalId = 3L; // ID del repartidor actual

    // === Métodos para tests ===
    public void setServicioPedidos(IServicioPedidosUI servicioPedidos) {
        this.servicioPedidos = servicioPedidos;
    }

    @FXML
    public void initialize() {
        if (this.servicioPedidos == null) {
            this.servicioPedidos = com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl.getInstance();
        }
        lblEstadoConexion.setText("✅ Conectado");
        
        // Registrar listener de eventos en tiempo real para cambios de estado
        if (this.servicioPedidos instanceof com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl impl) {
            impl.addPropertyChangeListener(evt -> Platform.runLater(this::cargarColas));
        }

        cargarColas();
    }

    @FXML
    private void cargarColas() {
        if (servicioPedidos != null) {
            // Cargar pedidos LISTOS
            servicioPedidos.obtenerPedidosPorEstado(CodigoEstadoPedido.LISTO)
                    .thenAccept(pedidos -> Platform.runLater(() -> {
                        contenedorListos.getChildren().clear();
                        if (pedidos != null && !pedidos.isEmpty()) {
                            for (PedidoDTO pedido : pedidos) {
                                agregarTarjetaListo(pedido);
                            }
                        } else {
                            mostrarMensaje(contenedorListos, "📦 No hay pedidos listos para retirar");
                        }
                    }));

            // Cargar pedidos EN_CAMINO
            servicioPedidos.obtenerPedidosPorEstado(CodigoEstadoPedido.EN_CAMINO)
                    .thenAccept(pedidos -> Platform.runLater(() -> {
                        contenedorEnCamino.getChildren().clear();
                        if (pedidos != null && !pedidos.isEmpty()) {
                            for (PedidoDTO pedido : pedidos) {
                                agregarTarjetaEnCamino(pedido);
                            }
                        } else {
                            mostrarMensaje(contenedorEnCamino, "🚚 No hay pedidos en camino");
                        }
                    }));
        } else {
            mostrarMensaje(contenedorListos, "📦 No hay pedidos listos para retirar");
            mostrarMensaje(contenedorEnCamino, "🚚 No hay pedidos en camino");
        }
    }

    private void mostrarMensaje(VBox contenedor, String texto) {
        contenedor.getChildren().clear();
        Label mensaje = new Label(texto);
        mensaje.setStyle("-fx-padding: 20; -fx-text-fill: #666;");
        contenedor.getChildren().add(mensaje);
    }

    private void agregarTarjetaListo(PedidoDTO pedido) {
        VBox tarjeta = crearTarjetaBase(pedido);
        Button btnIniciar = new Button("🚚 Iniciar Entrega");
        btnIniciar.setOnAction(e -> iniciarEntrega(pedido));
        tarjeta.getChildren().add(btnIniciar);
        contenedorListos.getChildren().add(tarjeta);
    }

    private void agregarTarjetaEnCamino(PedidoDTO pedido) {
        VBox tarjeta = crearTarjetaBase(pedido);
        Button btnConfirmar = new Button("✅ Confirmar Entrega");
        btnConfirmar.setOnAction(e -> confirmarEntrega(pedido));
        tarjeta.getChildren().add(btnConfirmar);
        contenedorEnCamino.getChildren().add(tarjeta);
    }

    private VBox crearTarjetaBase(PedidoDTO pedido) {
        VBox tarjeta = new VBox(5);
        tarjeta.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label codigo = new Label("📄 " + pedido.codigoPedido());
        codigo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label cliente = new Label("👤 " + pedido.nombreCliente());
        Label direccion = new Label("📍 " + pedido.direccionEntrega());
        direccion.setWrapText(true);

        tarjeta.getChildren().addAll(codigo, cliente, direccion);
        return tarjeta;
    }

    // Package-private para acceso desde tests
    void iniciarEntrega(PedidoDTO pedido) {
        if (servicioPedidos != null) {
            servicioPedidos.transicionarEstado(pedido.id(), CodigoEstadoPedido.EN_CAMINO, personalId)
                    .thenAccept(resultado -> Platform.runLater(() -> {
                        if (resultado) {
                            mostrarNotificacion("🚚 Entrega iniciada para " + pedido.codigoPedido());
                            cargarColas();
                        } else {
                            mostrarNotificacion("⚠️ No se pudo iniciar la entrega");
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarNotificacion("❌ Error al procesar"));
                        return null;
                    });
        } else {
            mostrarNotificacion("🚚 Demo: Entrega iniciada para " + pedido.codigoPedido());
            cargarColas();
        }
    }

    // Package-private para acceso desde tests
    void confirmarEntrega(PedidoDTO pedido) {
        if (servicioPedidos != null) {
            servicioPedidos.transicionarEstado(pedido.id(), CodigoEstadoPedido.ENTREGADO, personalId)
                    .thenAccept(resultado -> Platform.runLater(() -> {
                        if (resultado) {
                            mostrarNotificacion("✅ Pedido " + pedido.codigoPedido() + " entregado");
                            cargarColas();
                        } else {
                            mostrarNotificacion("⚠️ No se pudo confirmar la entrega");
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarNotificacion("❌ Error al procesar"));
                        return null;
                    });
        } else {
            mostrarNotificacion("✅ Demo: Pedido " + pedido.codigoPedido() + " entregado");
            cargarColas();
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
        Stage stage = (Stage) contenedorListos.getScene().getWindow();
        stage.close();
    }
}