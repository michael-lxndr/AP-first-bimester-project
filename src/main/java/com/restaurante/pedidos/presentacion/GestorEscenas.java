package com.restaurante.pedidos.presentacion;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class GestorEscenas {
	private static final String MAIN_VIEW = "/com/restaurante/pedidos/presentacion/vista/vista-principal.fxml";
	private static final String ADMIN_VIEW = "/com/restaurante/pedidos/presentacion/vista/vista-administrador.fxml";
	private static final String COOK_VIEW = "/com/restaurante/pedidos/presentacion/vista/vista-cocinero.fxml";
	private static final String DELIVERY_VIEW = "/com/restaurante/pedidos/presentacion/vista/vista-repartidor.fxml";
	private static final String CUSTOMER_VIEW = "/com/restaurante/pedidos/presentacion/vista/vista-cliente.fxml";
	private static final String APPLICATION_CSS = "/com/restaurante/pedidos/presentacion/estilo/aplicacion.css";
	private static final double WINDOW_WIDTH = 720;
	private static final double WINDOW_HEIGHT = 520;
	private static GestorEscenas current;

	private final CargadorVistas cargadorVistas = new CargadorVistas();
	private Stage primaryStage;

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		current = this;
	}

	public static GestorEscenas getCurrent() {
		if (current == null) {
			throw new IllegalStateException("GestorEscenas no fue inicializado.");
		}

		return current;
	}

	public void mostrarVistaPrincipal() {
		mostrarVista(MAIN_VIEW, "Gestor de Pedidos del Restaurante");
	}

	public void mostrarPantallasOperativas() {
		mostrarVista(primaryStage, ADMIN_VIEW, "Administrador", 40, 40);
		mostrarVista(new Stage(), COOK_VIEW, "Cocinero", 800, 40);
		mostrarVista(new Stage(), DELIVERY_VIEW, "Repartidor", 40, 620);
		mostrarVista(new Stage(), CUSTOMER_VIEW, "Cliente", 800, 620);
	}

	public void mostrarVistaAdministrador() {
		mostrarVista(ADMIN_VIEW, "Administración de personal");
	}

	public void mostrarVistaCocinero() {
		mostrarVista(COOK_VIEW, "Panel de cocinero");
	}

	public void mostrarVistaRepartidor() {
		mostrarVista(DELIVERY_VIEW, "Panel de repartidor");
	}

	public void mostrarVistaCliente() {
		mostrarVista(CUSTOMER_VIEW, "Panel de cliente");
	}

	private void mostrarVista(String rutaFxml, String titulo) {
		mostrarVista(primaryStage, rutaFxml, titulo, null, null);
	}

	private void mostrarVista(Stage stage, String rutaFxml, String titulo, Integer posicionX, Integer posicionY) {
		Parent root = cargadorVistas.cargar(rutaFxml);
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

		String stylesheet = Objects.requireNonNull(
			getClass().getResource(APPLICATION_CSS),
			"Archivo CSS no encontrado: " + APPLICATION_CSS
		).toExternalForm();

		scene.getStylesheets().add(stylesheet);

		stage.setTitle(titulo);
		stage.setScene(scene);
		if (posicionX != null && posicionY != null) {
			stage.setX(posicionX);
			stage.setY(posicionY);
		} else {
			stage.centerOnScreen();
		}
		stage.show();
	}
}
