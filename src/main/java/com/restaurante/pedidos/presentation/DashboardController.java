package com.restaurante.pedidos.presentation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
	@FXML
	private Label statusLabel;

	@FXML
	private void openAdminView() {
		StageManager.getCurrent().showAdminView();
	}

	@FXML
	private void openCookView() {
		StageManager.getCurrent().showCookView();
	}

	@FXML
	private void openDeliveryView() {
		StageManager.getCurrent().showDeliveryView();
	}

	@FXML
	private void openCustomerView() {
		StageManager.getCurrent().showCustomerView();
	}

	@FXML
	private void exitApplication() {
		Platform.exit();
	}

	@FXML
	private void initialize() {
		statusLabel.setText("Seleccioná una pantalla para ingresar al sistema");
	}
}
