package first.bimester.presentation.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {
	@Override
	public void start(Stage primaryStage) {
		StageManager stageManager = new StageManager();
		stageManager.setPrimaryStage(primaryStage);
		stageManager.showMainView();
	}

	@Override
	public void stop() {
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
