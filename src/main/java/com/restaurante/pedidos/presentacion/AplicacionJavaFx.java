package com.restaurante.pedidos.presentacion;

import com.restaurante.pedidos.configuracion.ConfiguracionBaseDatos;
import com.restaurante.pedidos.configuracion.ConfiguracionHilos;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class AplicacionJavaFx extends Application {
	@Override
	public void start(Stage primaryStage) {
		GestorEscenas gestorEscenas = new GestorEscenas();
		gestorEscenas.setPrimaryStage(primaryStage);
		gestorEscenas.mostrarPantallasOperativas();
	}

	@Override
	public void stop() {
		ConfiguracionHilos.shutdown();
		ConfiguracionBaseDatos.cerrar();
		Platform.exit();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
