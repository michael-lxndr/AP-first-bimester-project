package com.restaurante.pedidos.presentacion;

import com.restaurante.pedidos.dominio.CodigoRol;
import com.restaurante.pedidos.dominio.entidad.Personal;
import com.restaurante.pedidos.servicio.ServicioPersonal;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ControladorAdministrador {
	private final ServicioPersonal servicioPersonal = new ServicioPersonal();

	@FXML
	private TableView<Personal> staffTable;
	@FXML
	private TableColumn<Personal, Long> idColumn;
	@FXML
	private TableColumn<Personal, String> roleColumn;
	@FXML
	private TableColumn<Personal, String> fullNameColumn;
	@FXML
	private TableColumn<Personal, String> usernameColumn;
	@FXML
	private TableColumn<Personal, String> emailColumn;
	@FXML
	private TableColumn<Personal, String> activeColumn;
	@FXML
	private ComboBox<CodigoRol> roleComboBox;
	@FXML
	private TextField fullNameField;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField usernameField;
	@FXML
	private CheckBox activeCheckBox;
	@FXML
	private Label messageLabel;

	@FXML
	private void initialize() {
		roleComboBox.setItems(FXCollections.observableArrayList(CodigoRol.values()));
		roleComboBox.getSelectionModel().select(CodigoRol.COOK);
		activeCheckBox.setSelected(true);

		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatRol(cell.getValue())));
		activeColumn.setCellValueFactory(cell -> new SimpleStringProperty(Boolean.TRUE.equals(cell.getValue().getIsActive()) ? "Activo" : "Inactivo"));

		staffTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedPersonal) -> fillForm(selectedPersonal));
		loadPersonal();
	}

	@FXML
	private void crearPersonal() {
		try {
			validateForm();
			servicioPersonal.crear(
				roleComboBox.getValue(),
				fullNameField.getText().trim(),
				phoneField.getText().trim(),
				emailField.getText().trim(),
				usernameField.getText().trim(),
				activeCheckBox.isSelected()
			);

			showMessage("Personal creado correctamente.");
			limpiarFormulario();
			loadPersonal();
		} catch (RuntimeException exception) {
			showMessage("Error al crear: " + exception.getMessage());
		}
	}

	@FXML
	private void modificarPersonal() {
		Personal selectedPersonal = getSelectedPersonal();
		if (selectedPersonal == null) {
			showMessage("Seleccioná un registro para modificar.");
			return;
		}

		try {
			validateForm();
			servicioPersonal.modificar(
				selectedPersonal.getId(),
				roleComboBox.getValue(),
				fullNameField.getText().trim(),
				phoneField.getText().trim(),
				emailField.getText().trim(),
				usernameField.getText().trim(),
				activeCheckBox.isSelected()
			);

			showMessage("Personal actualizado correctamente.");
			loadPersonal();
		} catch (RuntimeException exception) {
			showMessage("Error al modificar: " + exception.getMessage());
		}
	}

	@FXML
	private void alternarActivo() {
		Personal selectedPersonal = getSelectedPersonal();
		if (selectedPersonal == null) {
			showMessage("Seleccioná un registro para dar de alta o baja.");
			return;
		}

		boolean newActiveValue = !Boolean.TRUE.equals(selectedPersonal.getIsActive());
		servicioPersonal.establecerActivo(selectedPersonal.getId(), newActiveValue);
		showMessage(newActiveValue ? "Personal dado de alta." : "Personal dado de baja.");
		loadPersonal();
	}

	@FXML
	private void eliminarPersonal() {
		Personal selectedPersonal = getSelectedPersonal();
		if (selectedPersonal == null) {
			showMessage("Seleccioná un registro para eliminar.");
			return;
		}

		try {
			servicioPersonal.eliminar(selectedPersonal.getId());
			showMessage("Personal eliminado correctamente.");
			limpiarFormulario();
			loadPersonal();
		} catch (RuntimeException exception) {
			showMessage("No se pudo eliminar. Si tiene pedidos asociados, usá baja lógica. Detalle: " + exception.getMessage());
		}
	}

	@FXML
	private void limpiarFormulario() {
		staffTable.getSelectionModel().clearSelection();
		roleComboBox.getSelectionModel().select(CodigoRol.COOK);
		fullNameField.clear();
		phoneField.clear();
		emailField.clear();
		usernameField.clear();
		activeCheckBox.setSelected(true);
	}

	@FXML
	private void cerrarVentana(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	private void loadPersonal() {
		try {
			staffTable.setItems(FXCollections.observableArrayList(servicioPersonal.buscarTodos()));
		} catch (RuntimeException exception) {
			showMessage("No se pudo conectar a la base de datos: " + exception.getMessage());
			staffTable.setItems(FXCollections.observableArrayList());
		}
	}

	private void fillForm(Personal staff) {
		if (staff == null) {
			return;
		}

		roleComboBox.getSelectionModel().select(staff.getRol().getCodigoRol());
		fullNameField.setText(staff.getFullName());
		phoneField.setText(staff.getPhone());
		emailField.setText(staff.getEmail());
		usernameField.setText(staff.getUsername());
		activeCheckBox.setSelected(Boolean.TRUE.equals(staff.getIsActive()));
	}

	private Personal getSelectedPersonal() {
		return staffTable.getSelectionModel().getSelectedItem();
	}

	private void validateForm() {
		if (roleComboBox.getValue() == null) {
			throw new IllegalArgumentException("El rol es obligatorio.");
		}
		if (fullNameField.getText().isBlank()) {
			throw new IllegalArgumentException("El nombre completo es obligatorio.");
		}
		if (emailField.getText().isBlank()) {
			throw new IllegalArgumentException("El email es obligatorio.");
		}
		if (usernameField.getText().isBlank()) {
			throw new IllegalArgumentException("El usuario es obligatorio.");
		}
	}

	private String formatRol(Personal staff) {
		return switch (staff.getRol().getCodigoRol()) {
			case ADMINISTRATOR -> "Administrador";
			case COOK -> "Cocinero";
			case COURIER -> "Entrega";
		};
	}

	private void showMessage(String message) {
		messageLabel.setText(message);
	}
}
