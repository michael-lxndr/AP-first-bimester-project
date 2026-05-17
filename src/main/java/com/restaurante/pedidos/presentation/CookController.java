package com.restaurante.pedidos.presentation;

import javafx.fxml.FXML;

public class CookController {
	@FXML
	private void goBack() {
		StageManager.getCurrent().showMainView();
	}
}
