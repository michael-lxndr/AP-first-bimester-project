package com.restaurante.pedidos.presentacion;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class GestorEscenas {
	private static final String RUTA_VISTA_PRINCIPAL = "/com/restaurante/pedidos/presentacion/vista/vista-principal.fxml";
	private static final String RUTA_VISTA_ADMINISTRADOR = "/com/restaurante/pedidos/presentacion/vista/vista-administrador.fxml";
	private static final String RUTA_VISTA_COCINERO = "/com/restaurante/pedidos/presentacion/vista/vista-cocinero.fxml";
	private static final String RUTA_VISTA_REPARTIDOR = "/com/restaurante/pedidos/presentacion/vista/vista-repartidor.fxml";
	private static final String RUTA_VISTA_CLIENTE = "/com/restaurante/pedidos/presentacion/vista/vista-cliente.fxml";
	private static final String RUTA_CSS_APLICACION = "/com/restaurante/pedidos/presentacion/estilo/aplicacion.css";
	private static final double ANCHO_VENTANA = 720;
	private static final double ALTO_VENTANA = 520;
	private static GestorEscenas actual;

	private final CargadorVistas cargadorVistas = new CargadorVistas();
	private Stage escenarioPrincipal;

	public void setPrimaryStage(Stage escenarioPrincipal) {
		this.escenarioPrincipal = escenarioPrincipal;
		actual = this;
	}

	public static GestorEscenas getCurrent() {
		if (actual == null) {
			throw new IllegalStateException("GestorEscenas no fue inicializado.");
		}

		return actual;
	}

	public void mostrarVistaPrincipal() {
		mostrarVista(RUTA_VISTA_PRINCIPAL, "Gestor de Pedidos del Restaurante");
	}

	public void mostrarPantallasOperativas() {
		mostrarVista(escenarioPrincipal, RUTA_VISTA_ADMINISTRADOR, "Administrador", 40, 40);
		mostrarVista(new Stage(), RUTA_VISTA_COCINERO, "Cocinero", 800, 40);
		mostrarVista(new Stage(), RUTA_VISTA_REPARTIDOR, "Repartidor", 40, 620);
		mostrarVista(new Stage(), RUTA_VISTA_CLIENTE, "Cliente", 800, 620);
	}

	public void mostrarVistaAdministrador() {
		mostrarVista(RUTA_VISTA_ADMINISTRADOR, "Administración de personal");
	}

	public void mostrarVistaCocinero() {
		mostrarVista(RUTA_VISTA_COCINERO, "Panel de cocinero");
	}

	public void mostrarVistaRepartidor() {
		mostrarVista(RUTA_VISTA_REPARTIDOR, "Panel de repartidor");
	}

	public void mostrarVistaCliente() {
		mostrarVista(RUTA_VISTA_CLIENTE, "Panel de cliente");
	}

	private void mostrarVista(String rutaFxml, String titulo) {
		mostrarVista(escenarioPrincipal, rutaFxml, titulo, null, null);
	}

	private void mostrarVista(Stage stage, String rutaFxml, String titulo, Integer posicionX, Integer posicionY) {
		Parent root = cargadorVistas.cargar(rutaFxml);
		Scene scene = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);

		String stylesheet = Objects.requireNonNull(
			getClass().getResource(RUTA_CSS_APLICACION),
			"Archivo CSS no encontrado: " + RUTA_CSS_APLICACION
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
