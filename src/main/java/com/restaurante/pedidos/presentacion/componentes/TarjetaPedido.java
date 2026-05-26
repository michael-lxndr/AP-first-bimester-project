package com.restaurante.pedidos.presentacion.componentes;

import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * Componente reutilizable: tarjeta visual para mostrar un pedido.
 *
 * Diseño:
 * - Inmutable después de creado (thread-safe).
 * - Actualizable mediante método público (sin recrear nodo).
 * - Estilizable vía CSS con clase base "tarjeta-pedido".
 */
public class TarjetaPedido extends VBox {

    private final Label lblCodigo, lblCliente, lblTotal, lblEstado, lblTiempo;
    private final Button btnAccion;
    private PedidoDTO pedidoActual;  // Solo para referencia interna

    public TarjetaPedido(
            PedidoDTO pedido,
            java.util.function.Consumer<PedidoDTO> onAccion,
            String textoBoton,
            String estiloEstadoBase  // Ej: "estado-en-preparacion"
    ) {
        this.pedidoActual = pedido;

        // Construir UI
        setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        setPadding(new Insets(12));
        setSpacing(8);
        getStyleClass().add("tarjeta-pedido");

        // Header: código + estado
        var header = new HBox(10);
        lblCodigo = new Label(pedido.codigoPedido());
        lblCodigo.getStyleClass().add("label-codigo");
        lblEstado = new Label(pedido.estadoActual().descripcion());
        lblEstado.getStyleClass().add("label-estado");
        lblEstado.getStyleClass().add(pedido.getCssClassParaEstado());
        header.getChildren().addAll(lblCodigo, new Region(), lblEstado);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        // Cuerpo: cliente + items resumen + total
        lblCliente = new Label("👤 " + pedido.nombreCliente());
        lblCliente.getStyleClass().add("label-cliente");

        var itemsResumen = new Label(formatoResumenItems(pedido.items()));
        itemsResumen.setWrapText(true);
        itemsResumen.getStyleClass().add("label-items");

        lblTotal = new Label("💰 $" + pedido.total().toPlainString());
        lblTotal.getStyleClass().add("label-total");

        // Footer: tiempo + botón
        var footer = new HBox(10);
        lblTiempo = new Label("⏱ " + pedido.getTiempoRestanteParaUI());
        lblTiempo.getStyleClass().add("label-tiempo");

        btnAccion = new Button(textoBoton);
        btnAccion.getStyleClass().add("btn-accion");
        btnAccion.setOnAction(e -> {
            if (onAccion != null) {
                onAccion.accept(pedidoActual);
            }
        });

        footer.getChildren().addAll(lblTiempo, new Region(), btnAccion);
        HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);

        // Ensamblar
        getChildren().addAll(header, lblCliente, itemsResumen, lblTotal, new Separator(), footer);
    }

    /**
     * Actualiza el estado visual sin recrear el componente.
     * Útil cuando el servicio notifica cambios.
     */
    public void actualizarEstado(PedidoDTO pedidoActualizado) {
        if (pedidoActualizado == null) return;

        this.pedidoActual = pedidoActualizado;

        // Actualizar solo lo que cambió (eficiente)
        if (pedidoActualizado.estadoActual() != null) {
            lblEstado.setText(pedidoActualizado.estadoActual().descripcion());
            // Remover clases anteriores de estado
            lblEstado.getStyleClass().removeIf(c -> c.startsWith("estado-"));
            lblEstado.getStyleClass().add(pedidoActualizado.getCssClassParaEstado());
        }
        lblTiempo.setText("⏱ " + pedidoActualizado.getTiempoRestanteParaUI());

        // Animación sutil para indicar cambio
        fadeIn();
    }

    private String formatoResumenItems(java.util.List<com.restaurante.pedidos.dominio.dto.ItemPedidoDTO> items) {
        if (items == null || items.isEmpty()) return "Sin items";
        if (items.size() == 1) {
            var item = items.get(0);
            return item.cantidad() + "x " + item.nombreProducto();
        }
        return items.size() + " items (" +
                items.stream().mapToInt(com.restaurante.pedidos.dominio.dto.ItemPedidoDTO::cantidad).sum() + " unidades)";
    }

    private void fadeIn() {
        // Animación simple: se puede mejorar con JavaFX CSS transitions
        setOpacity(0.7);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.millis(200), this);
        ft.setFromValue(0.7);
        ft.setToValue(1.0);
        ft.play();
    }

    // Getters para tests
    public PedidoDTO getPedidoActual() { return pedidoActual; }
    public String getEstadoTexto() { return lblEstado.getText(); }
}