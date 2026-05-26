package com.restaurante.pedidos.presentacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de aplicación JavaFX, que actúa como el lanzador de interfaz gráfica.
 */
public class AplicacionJavaFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        abrirVentana("/vista/vista-principal.fxml", "Restaurant Delivery - Panel Principal", 600, 400);
    }

    public void abrirVentana(String fxmlPath, String titulo, int ancho, int alto) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(root, ancho, alto));

        // Cargar CSS si existe
        var css = getClass().getResource("/estilo/aplicacion.css");
        if (css != null) {
            stage.getScene().getStylesheets().add(css.toExternalForm());
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
