package com.restaurante.pedidos.presentation;

import javafx.fxml.FXML;

public class CustomerController {
	@FXML
	private void goBack() {
		StageManager.getCurrent().showMainView();
	}
}
