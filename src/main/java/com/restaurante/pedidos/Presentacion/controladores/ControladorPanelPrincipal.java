package com.restaurante.pedidos.presentacion.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorPanelPrincipal {

    @FXML
    private Label statusLabel;

    @FXML
    private void abrirVistaAdministrador() {
        abrirVentana("vista-administrador.fxml", "Administrador - Gestión de Personal");
    }

    @FXML
    private void abrirVistaCocinero() {
        abrirVentana("vista-cocinero.fxml", "Cocinero - Cola de Preparación");
    }

    @FXML
    private void abrirVistaRepartidor() {
        abrirVentana("vista-repartidor.fxml", "Repartidor - Entregas");
    }

    @FXML
    private void abrirVistaCliente() {
        abrirVentana("vista-cliente.fxml", "Cliente - Consultar Pedido");
    }

    @FXML
    private void abrirVistaSimulacion() {
        abrirVentana("vista-simulacion.fxml", "Simulación Concurrente Multihilo");
    }

    private void abrirVentana(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/" + fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();

            if (statusLabel != null) {
                statusLabel.setText("Abierto: " + titulo);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana: " + fxmlPath + "\n" + e.getMessage());
        }
    }

    @FXML
    private void salirAplicacion() {
        System.exit(0);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}