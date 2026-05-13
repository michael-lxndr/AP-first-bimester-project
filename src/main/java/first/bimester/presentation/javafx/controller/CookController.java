package first.bimester.presentation.javafx.controller;

import first.bimester.presentation.javafx.StageManager;
import javafx.fxml.FXML;

public class CookController {
	@FXML
	private void goBack() {
		StageManager.getCurrent().showMainView();
	}
}
