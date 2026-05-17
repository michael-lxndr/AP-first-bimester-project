package com.restaurante.pedidos.presentation;

import com.restaurante.pedidos.config.DatabaseConfig;
import com.restaurante.pedidos.config.ThreadPoolConfig;
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
		ThreadPoolConfig.shutdown();
		DatabaseConfig.close();
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
