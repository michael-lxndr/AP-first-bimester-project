package com.restaurante.pedidos.presentation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ViewLoader {
	public Parent load(String fxmlPath) {
		try {
			URL resource = Objects.requireNonNull(
				getClass().getResource(fxmlPath),
				"FXML file not found: " + fxmlPath
			);

			FXMLLoader loader = new FXMLLoader(resource);

			return loader.load();
		} catch (IOException exception) {
			throw new IllegalStateException("Could not load FXML file: " + fxmlPath, exception);
		}
	}
}
