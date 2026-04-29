package first.bimester.project.restaurant.presentation.javafx;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StageManager {
	private static final String MAIN_VIEW = "/first/bimester/project/restaurant/presentation/javafx/view/main-view.fxml";
	private static final String APPLICATION_CSS = "/first/bimester/project/restaurant/presentation/javafx/style/application.css";

	private final ViewLoader viewLoader;
	private Stage primaryStage;

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void showMainView() {
		showView(MAIN_VIEW, "Restaurant Order Manager");
	}

	private void showView(String fxmlPath, String title) {
		Parent root = viewLoader.load(fxmlPath);
		Scene scene = new Scene(root, 900, 600);

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
