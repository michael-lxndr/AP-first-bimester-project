package first.bimester.project.restaurant.presentation.javafx.controller;

import first.bimester.project.restaurant.presentation.javafx.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class MainController {
	private final StageManager stageManager;

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
