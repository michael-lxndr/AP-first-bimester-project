package com.restaurante.pedidos.presentation;

import javafx.fxml.FXML;

public class DeliveryController {
	@FXML
	private void goBack() {
		StageManager.getCurrent().showMainView();
	}
}
