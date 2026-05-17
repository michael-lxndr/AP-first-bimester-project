package com.restaurante.pedidos.presentacion;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class CargadorVistas {
	public Parent cargar(String rutaFxml) {
		try {
			URL recurso = Objects.requireNonNull(
				getClass().getResource(rutaFxml),
				"Archivo FXML no encontrado: " + rutaFxml
			);

			FXMLLoader loader = new FXMLLoader(recurso);

			return loader.load();
		} catch (IOException exception) {
			throw new IllegalStateException("No se pudo cargar el archivo FXML: " + rutaFxml, exception);
		}
	}
}
