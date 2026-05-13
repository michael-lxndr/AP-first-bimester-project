package first.bimester.presentation.javafx.controller;

import first.bimester.presentation.javafx.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
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
	private void openCourierView() {
		StageManager.getCurrent().showCourierView();
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
