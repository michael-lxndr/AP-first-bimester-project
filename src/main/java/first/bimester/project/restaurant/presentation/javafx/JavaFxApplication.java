package first.bimester.project.restaurant.presentation.javafx;

import first.bimester.project.restaurant.FirstBimesterProjectApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class JavaFxApplication extends Application {
	private ConfigurableApplicationContext applicationContext;

	@Override
	public void init() {
		applicationContext = new SpringApplicationBuilder(FirstBimesterProjectApplication.class)
			.web(WebApplicationType.NONE)
			.properties(Map.of("app.ui", "javafx"))
			.run();
	}

	@Override
	public void start(Stage primaryStage) {
		StageManager stageManager = applicationContext.getBean(StageManager.class);
		stageManager.setPrimaryStage(primaryStage);
		stageManager.showMainView();
	}

	@Override
	public void stop() {
		if (applicationContext != null) {
			applicationContext.close();
		}

		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
