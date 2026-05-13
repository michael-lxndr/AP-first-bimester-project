package first.bimester.presentation.javafx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainController {
	@FXML
	private void openAdminView() {
		// Después conectamos esto a StageManager.showAdminView().
		System.out.println("Open admin view");
	}

	@FXML
	private void openCookView() {
		System.out.println("Open cook view");
	}

	@FXML
	private void openCourierView() {
		System.out.println("Open courier view");
	}

	@FXML
	private void openCustomerView() {
		System.out.println("Open customer view");
	}

	@FXML
	private void exitApplication() {
		Platform.exit();
	}
}
