package com.restaurante.pedidos.presentacion;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ControladorPanelPrincipal {
	@FXML
	private Label statusLabel;

	@FXML
	private void abrirVistaAdministrador() {
		GestorEscenas.getCurrent().mostrarVistaAdministrador();
	}

	@FXML
	private void abrirVistaCocinero() {
		GestorEscenas.getCurrent().mostrarVistaCocinero();
	}

	@FXML
	private void abrirVistaRepartidor() {
		GestorEscenas.getCurrent().mostrarVistaRepartidor();
	}

	@FXML
	private void abrirVistaCliente() {
		GestorEscenas.getCurrent().mostrarVistaCliente();
	}

	@FXML
	private void salirAplicacion() {
		Platform.exit();
	}

	@FXML
	private void initialize() {
		statusLabel.setText("Seleccioná una pantalla para ingresar al sistema");
	}
}
