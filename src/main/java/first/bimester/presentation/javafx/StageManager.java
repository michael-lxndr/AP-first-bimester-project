package first.bimester.presentation.javafx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class StageManager {
	private static final String MAIN_VIEW = "/first/bimester/presentation/javafx/view/main-view.fxml";
	private static final String ADMIN_VIEW = "/first/bimester/presentation/javafx/view/admin-view.fxml";
	private static final String COOK_VIEW = "/first/bimester/presentation/javafx/view/cook-view.fxml";
	private static final String COURIER_VIEW = "/first/bimester/presentation/javafx/view/courier-view.fxml";
	private static final String CUSTOMER_VIEW = "/first/bimester/presentation/javafx/view/customer-view.fxml";
	private static final String APPLICATION_CSS = "/first/bimester/presentation/javafx/style/application.css";
	private static StageManager current;

	private final ViewLoader viewLoader = new ViewLoader();
	private Stage primaryStage;

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		current = this;
	}

	public static StageManager getCurrent() {
		if (current == null) {
			throw new IllegalStateException("StageManager has not been initialized yet.");
		}

		return current;
	}

	public void showMainView() {
		showView(MAIN_VIEW, "Restaurant Order Manager");
	}

	public void showAdminView() {
		showView(ADMIN_VIEW, "Administración de personal");
	}

	public void showCookView() {
		showView(COOK_VIEW, "Panel de cocinero");
	}

	public void showCourierView() {
		showView(COURIER_VIEW, "Panel de delivery");
	}

	public void showCustomerView() {
		showView(CUSTOMER_VIEW, "Panel de cliente");
	}

	private void showView(String fxmlPath, String title) {
		Parent root = viewLoader.load(fxmlPath);
		Scene scene = new Scene(root, 1050, 680);

		String stylesheet = Objects.requireNonNull(
			getClass().getResource(APPLICATION_CSS),
			"CSS file not found: " + APPLICATION_CSS
		).toExternalForm();

		scene.getStylesheets().add(stylesheet);

		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
}
