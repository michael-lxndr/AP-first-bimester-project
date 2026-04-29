package first.bimester.project.restaurant.presentation.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ViewLoader {
	private final ApplicationContext applicationContext;

	public Parent load(String fxmlPath) {
		try {
			URL resource = Objects.requireNonNull(
				getClass().getResource(fxmlPath),
				"FXML file not found: " + fxmlPath
			);

			FXMLLoader loader = new FXMLLoader(resource);

			// Esto permite que los controllers sean beans de Spring.
			loader.setControllerFactory(applicationContext::getBean);

			return loader.load();
		} catch (IOException exception) {
			throw new IllegalStateException("Could not load FXML file: " + fxmlPath, exception);
		}
	}
}
