package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.dominio.servicio.ui.IServicioPedidosUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControladorCliente {

    @FXML private TextField campoCodigo;
    @FXML private Label lblMensaje;
    @FXML private VBox panelResultado;
    @FXML private Label lblCodigoPedido;
    @FXML private Label lblEstado;
    @FXML private Label lblTiempoRestante;
    @FXML private Label lblCliente;
    @FXML private Label lblDireccion;
    @FXML private Label lblTotal;
    @FXML private Label lblPrioritario;
    @FXML private Label lblItems;

    private IServicioPedidosUI servicioPedidos;

    // ✅ Setter para inyección de dependencia (necesario para tests)
    public void setServicioPedidos(IServicioPedidosUI servicioPedidos) {
        this.servicioPedidos = servicioPedidos;
    }

    @FXML
    public void initialize() {
        if (this.servicioPedidos == null) {
            this.servicioPedidos = com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl.getInstance();
        }
        panelResultado.setVisible(false);
        panelResultado.setManaged(false);
        
        // Registrar listener de eventos en tiempo real para cambios de estado
        if (this.servicioPedidos instanceof com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl impl) {
            impl.addPropertyChangeListener(evt -> {
                if (evt.getNewValue() instanceof PedidoDTO updated) {
                    Platform.runLater(() -> {
                        String codigoActual = campoCodigo.getText().trim();
                        if (!codigoActual.isEmpty() && updated.codigoPedido().equalsIgnoreCase(codigoActual)) {
                            mostrarPedido(updated);
                        }
                    });
                }
            });
        }
    }

    @FXML
    private void consultar() {
        String codigo = campoCodigo.getText().trim();

        if (codigo.isEmpty()) {
            lblMensaje.setText("⚠️ Ingrese un código de pedido");
            return;
        }

        lblMensaje.setText("🔍 Consultando pedido: " + codigo);

        if (servicioPedidos != null) {
            servicioPedidos.consultarPorCodigo(codigo)
                    .thenAccept(resultado -> Platform.runLater(() -> {
                        if (resultado.isPresent()) {
                            mostrarPedido(resultado.get());
                        } else {
                            mostrarNoEncontrado();
                        }
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() -> mostrarError(ex.getMessage()));
                        return null;
                    });
        } else {
            mostrarResultadoEjemplo(codigo);
        }
    }

    private void mostrarPedido(PedidoDTO pedido) {
        lblCodigoPedido.setText("📄 Código: " + pedido.codigoPedido());
        lblEstado.setText("📌 Estado: " + pedido.estadoActual().descripcion());
        lblCliente.setText("👤 Cliente: " + pedido.nombreCliente());
        lblDireccion.setText("📍 Dirección: " + pedido.direccionEntrega());
        lblTotal.setText("💰 Total: $" + pedido.total().toPlainString());
        lblPrioritario.setText("⭐ Prioritario: " + (pedido.prioritario() ? "Sí" : "No"));

        if (pedido.items() != null && !pedido.items().isEmpty()) {
            StringBuilder items = new StringBuilder();
            pedido.items().forEach(item ->
                    items.append("• ").append(item.cantidad()).append("x ")
                            .append(item.nombreProducto()).append("\n"));
            lblItems.setText(items.toString());
        } else {
            lblItems.setText("Sin items");
        }

        panelResultado.setVisible(true);
        panelResultado.setManaged(true);
        lblMensaje.setText("✅ Pedido encontrado");
    }

    private void mostrarNoEncontrado() {
        lblMensaje.setText("❌ Pedido no encontrado");
        panelResultado.setVisible(false);
        panelResultado.setManaged(false);
    }

    private void mostrarError(String error) {
        lblMensaje.setText("❌ Error: " + error);
        panelResultado.setVisible(false);
        panelResultado.setManaged(false);
    }

    private void mostrarResultadoEjemplo(String codigo) {
        lblCodigoPedido.setText("📄 Código: " + codigo);
        lblEstado.setText("📌 Estado: EN_PREPARACIÓN");
        lblTiempoRestante.setText("⏱ Tiempo estimado: 25 min");
        lblCliente.setText("👤 Cliente: Cliente Ejemplo");
        lblDireccion.setText("📍 Dirección: Av. Principal 123, Quito");
        lblTotal.setText("💰 Total: $25.50");
        lblPrioritario.setText("⭐ Prioritario: No");
        lblItems.setText("• 2x Hamburguesa Clásica\n• 1x Papas Fritas\n• 2x Gaseosa");

        panelResultado.setVisible(true);
        panelResultado.setManaged(true);
        lblMensaje.setText("✅ Pedido encontrado (demo)");
    }

    @FXML
    private void limpiar() {
        campoCodigo.clear();
        lblMensaje.setText("");
        panelResultado.setVisible(false);
        panelResultado.setManaged(false);
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) campoCodigo.getScene().getWindow();
        stage.close();
    }
}