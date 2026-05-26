package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.dominio.dto.PedidoDTO;
import com.restaurante.pedidos.LogicaServicios.ServicioSimulacionConcurrente;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControladorSimulacion {

    @FXML private Label labelEstadoSimulacion;
    @FXML private ComboBox<String> comboIntervalo;
    @FXML private ComboBox<String> comboCocineros;
    @FXML private Button btnIniciar;
    @FXML private Button btnCerrar;

    // Buscador
    @FXML private TextField campoBuscarCodigo;
    @FXML private VBox panelResultadoBusqueda;
    @FXML private Label lblBusquedaCodigo;
    @FXML private Label lblBusquedaEstado;
    @FXML private Label lblBusquedaCliente;
    @FXML private Label lblBusquedaDireccion;
    @FXML private Label lblBusquedaTotal;
    @FXML private ProgressBar progressBusqueda;
    @FXML private Label lblBusquedaBitacora;
    @FXML private Label lblErrorBusqueda;

    // Terminal
    @FXML private TextArea areaLogsTerminal;

    // Tabla
    @FXML private TableView<PedidoDTO> tablaPedidosActivos;
    @FXML private TableColumn<PedidoDTO, Long> colID;
    @FXML private TableColumn<PedidoDTO, String> colCodigo;
    @FXML private TableColumn<PedidoDTO, String> colCliente;
    @FXML private TableColumn<PedidoDTO, String> colEstado;
    @FXML private TableColumn<PedidoDTO, Double> colProgreso;
    @FXML private TableColumn<PedidoDTO, BigDecimal> colTotal;

    private ServicioSimulacionConcurrente simulacion;
    private ObservableList<PedidoDTO> listaPedidos;
    private ScheduledExecutorService statusPoller;

    @FXML
    public void initialize() {
        simulacion = new ServicioSimulacionConcurrente();
        var sharedUI = com.restaurante.pedidos.dominio.servicio.ui.ServicioPedidosUIImpl.getInstance();
        sharedUI.registrarSimulador(simulacion);

        listaPedidos = FXCollections.observableArrayList();

        // Registrar listener de eventos en tiempo real para cambios de estado en la simulación
        sharedUI.addPropertyChangeListener(evt -> {
            if (evt.getNewValue() instanceof PedidoDTO updated) {
                Platform.runLater(() -> {
                    boolean found = false;
                    for (int i = 0; i < listaPedidos.size(); i++) {
                        if (java.util.Objects.equals(listaPedidos.get(i).id(), updated.id())) {
                            listaPedidos.set(i, updated);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        listaPedidos.add(updated);
                    }
                    tablaPedidosActivos.refresh();
                    
                    // Si el panel de búsqueda rápida está visible y muestra este pedido, actualizarlo
                    String busquedaActual = campoBuscarCodigo.getText().trim();
                    if (!busquedaActual.isEmpty() && updated.codigoPedido().equalsIgnoreCase(busquedaActual)) {
                        buscarPedidoCliente();
                    }
                });
            }
        });

        // 1. Cargar Combos
        comboIntervalo.getItems().addAll("2 segundos", "3 segundos", "4 segundos", "5 segundos");
        comboIntervalo.getSelectionModel().select(1); // 3s por defecto

        comboCocineros.getItems().addAll("1 Cocinero", "2 Cocineros", "3 Cocineros", "4 Cocineros", "5 Cocineros");
        comboCocineros.getSelectionModel().select(2); // 3 Cocineros por defecto

        // 2. Configurar Tabla
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoPedido"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Estado con texto legible
        colEstado.setCellValueFactory(cellData -> {
            var estado = cellData.getValue().estadoActual();
            return new SimpleStringProperty(estado != null ? estado.descripcion() : "—");
        });

        // Configurar columna de Progreso con ProgressBar personalizado
        colProgreso.setCellValueFactory(cellData -> {
            PedidoDTO p = cellData.getValue();
            double progresoVal = 0.1;
            if (p.estadoActual() != null && p.estadoActual().codigo() != null) {
                switch (p.estadoActual().codigo()) {
                    case "PENDIENTE": progresoVal = 0.1; break;
                    case "EN_PREPARACION": progresoVal = 0.4; break;
                    case "LISTO": progresoVal = 0.7; break;
                    case "EN_CAMINO": progresoVal = 0.9; break;
                    case "ENTREGADO": progresoVal = 1.0; break;
                }
            }
            return new SimpleObjectProperty<>(progresoVal);
        });

        colProgreso.setCellFactory(column -> new TableCell<PedidoDTO, Double>() {
            private final ProgressBar pb = new ProgressBar();
            {
                pb.setMaxWidth(Double.MAX_VALUE);
                pb.getStyleClass().add("custom-progress");
            }

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    pb.setProgress(item);
                    // Colorear según avance
                    if (item < 0.3) {
                        pb.setStyle("-fx-accent: #3b82f6;"); // Azul pendiente
                    } else if (item < 0.6) {
                        pb.setStyle("-fx-accent: #f59e0b;"); // Naranja cocina
                    } else if (item < 0.8) {
                        pb.setStyle("-fx-accent: #8b5cf6;"); // Púrpura listo
                    } else if (item < 1.0) {
                        pb.setStyle("-fx-accent: #10b981;"); // Verde en camino
                    } else {
                        pb.setStyle("-fx-accent: #059669;"); // Verde oscuro entregado
                    }
                    setGraphic(pb);
                }
            }
        });

        tablaPedidosActivos.setItems(listaPedidos);

        // 3. Vincular Callbacks del Motor de Simulación
        simulacion.setLogListener(mensaje -> Platform.runLater(() -> {
            areaLogsTerminal.appendText(mensaje + "\n");
            // Auto scroll
            areaLogsTerminal.selectPositionCaret(areaLogsTerminal.getLength());
            areaLogsTerminal.deselect();
        }));

        simulacion.setOrderUpdateListener(pedido -> Platform.runLater(() -> {
            updatePedidoEnTabla(pedido);
            actualizarDetalleBuscadorEnTiempoReal(pedido);
        }));

        btnCerrar.setDisable(true);
    }

    private void updatePedidoEnTabla(PedidoDTO pedido) {
        for (int i = 0; i < listaPedidos.size(); i++) {
            if (listaPedidos.get(i).id().equals(pedido.id())) {
                listaPedidos.set(i, pedido);
                tablaPedidosActivos.refresh();
                return;
            }
        }
        // Si no está, agregarlo
        listaPedidos.add(pedido);
    }

    private void actualizarDetalleBuscadorEnTiempoReal(PedidoDTO pedido) {
        if (panelResultadoBusqueda.isVisible() && lblBusquedaCodigo.getText().equals(pedido.codigoPedido())) {
            lblBusquedaEstado.setText(pedido.estadoActual() != null ? pedido.estadoActual().descripcion() : "—");
            double prog = 0.1;
            if (pedido.estadoActual() != null && pedido.estadoActual().codigo() != null) {
                switch (pedido.estadoActual().codigo()) {
                    case "PENDIENTE": prog = 0.1; break;
                    case "EN_PREPARACION": prog = 0.4; break;
                    case "LISTO": prog = 0.7; break;
                    case "EN_CAMINO": prog = 0.9; break;
                    case "ENTREGADO": prog = 1.0; break;
                }
            }
            progressBusqueda.setProgress(prog);
            lblBusquedaBitacora.setText(pedido.notasVisibles() != null ? pedido.notasVisibles() : "Preparando su pedido en cocina...");
        }
    }

    @FXML
    private void iniciarSimulacion() {
        int intervaloSegundos = comboIntervalo.getSelectionModel().getSelectedIndex() + 2; // 2 a 5s
        int cocineros = comboCocineros.getSelectionModel().getSelectedIndex() + 1; // 1 a 5

        listaPedidos.clear();
        areaLogsTerminal.clear();
        panelResultadoBusqueda.setVisible(false);
        panelResultadoBusqueda.setManaged(false);
        lblErrorBusqueda.setVisible(false);
        lblErrorBusqueda.setManaged(false);

        simulacion.startSimulation(intervaloSegundos, cocineros);

        btnIniciar.setDisable(true);
        btnCerrar.setDisable(false);
        comboIntervalo.setDisable(true);
        comboCocineros.setDisable(true);

        labelEstadoSimulacion.setText("ACTIVA");
        labelEstadoSimulacion.getStyleClass().removeAll("status-inactive", "status-draining");
        labelEstadoSimulacion.getStyleClass().add("status-active");

        // Iniciar un poller periódico de estado en la UI
        statusPoller = Executors.newSingleThreadScheduledExecutor();
        statusPoller.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            if (!simulacion.isSimulating()) {
                detenerPollerEstado();
                restablecerBotonesUI();
            }
        }), 1, 1, TimeUnit.SECONDS);
    }

    @FXML
    private void cerrarCocina() {
        simulacion.stopKitchen();
        btnCerrar.setDisable(true);
        labelEstadoSimulacion.setText("DRENANDO COLAS");
        labelEstadoSimulacion.getStyleClass().removeAll("status-active");
        labelEstadoSimulacion.getStyleClass().add("status-draining");
    }

    private void detenerPollerEstado() {
        if (statusPoller != null && !statusPoller.isShutdown()) {
            statusPoller.shutdown();
        }
    }

    private void restablecerBotonesUI() {
        btnIniciar.setDisable(false);
        btnCerrar.setDisable(true);
        comboIntervalo.setDisable(false);
        comboCocineros.setDisable(false);

        labelEstadoSimulacion.setText("INACTIVA");
        labelEstadoSimulacion.getStyleClass().removeAll("status-active", "status-draining");
        labelEstadoSimulacion.getStyleClass().add("status-inactive");
    }

    @FXML
    private void buscarPedidoCliente() {
        String codigo = campoBuscarCodigo.getText().trim();
        if (codigo.isEmpty()) return;

        Optional<PedidoDTO> encontrado = listaPedidos.stream()
                .filter(p -> p.codigoPedido().equalsIgnoreCase(codigo))
                .findFirst();

        if (encontrado.isPresent()) {
            PedidoDTO p = encontrado.get();
            lblBusquedaCodigo.setText(p.codigoPedido());
            lblBusquedaEstado.setText(p.estadoActual() != null ? p.estadoActual().descripcion() : "PENDIENTE");
            lblBusquedaCliente.setText("Cliente: " + p.nombreCliente());
            lblBusquedaDireccion.setText("Dirección: " + p.direccionEntrega());
            lblBusquedaTotal.setText(String.format("Total: $%s", p.total()));

            double prog = 0.1;
            if (p.estadoActual() != null && p.estadoActual().codigo() != null) {
                switch (p.estadoActual().codigo()) {
                    case "PENDIENTE": prog = 0.1; break;
                    case "EN_PREPARACION": prog = 0.4; break;
                    case "LISTO": prog = 0.7; break;
                    case "EN_CAMINO": prog = 0.9; break;
                    case "ENTREGADO": prog = 1.0; break;
                }
            }
            progressBusqueda.setProgress(prog);
            lblBusquedaBitacora.setText(p.notasVisibles() != null ? p.notasVisibles() : "Procesando pedido...");

            panelResultadoBusqueda.setVisible(true);
            panelResultadoBusqueda.setManaged(true);
            lblErrorBusqueda.setVisible(false);
            lblErrorBusqueda.setManaged(false);
        } else {
            panelResultadoBusqueda.setVisible(false);
            panelResultadoBusqueda.setManaged(false);
            lblErrorBusqueda.setVisible(true);
            lblErrorBusqueda.setManaged(true);
        }
    }

    public void detenerSimulacionInmediata() {
        detenerPollerEstado();
        if (simulacion != null) {
            simulacion.shutdownImmediately();
        }
    }
}
