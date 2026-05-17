package com.restaurante.pedidos.presentation;

import com.restaurante.pedidos.domain.RoleCode;
import com.restaurante.pedidos.domain.entity.Staff;
import com.restaurante.pedidos.service.StaffService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {
	private final StaffService staffService = new StaffService();

	@FXML
	private TableView<Staff> staffTable;
	@FXML
	private TableColumn<Staff, Long> idColumn;
	@FXML
	private TableColumn<Staff, String> roleColumn;
	@FXML
	private TableColumn<Staff, String> fullNameColumn;
	@FXML
	private TableColumn<Staff, String> usernameColumn;
	@FXML
	private TableColumn<Staff, String> emailColumn;
	@FXML
	private TableColumn<Staff, String> activeColumn;
	@FXML
	private ComboBox<RoleCode> roleComboBox;
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
		roleComboBox.setItems(FXCollections.observableArrayList(RoleCode.values()));
		roleComboBox.getSelectionModel().select(RoleCode.COOK);
		activeCheckBox.setSelected(true);

		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		roleColumn.setCellValueFactory(cell -> new SimpleStringProperty(formatRole(cell.getValue())));
		activeColumn.setCellValueFactory(cell -> new SimpleStringProperty(Boolean.TRUE.equals(cell.getValue().getIsActive()) ? "Activo" : "Inactivo"));

		staffTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedStaff) -> fillForm(selectedStaff));
		loadStaff();
	}

	@FXML
	private void createStaff() {
		try {
			validateForm();
			staffService.create(
				roleComboBox.getValue(),
				fullNameField.getText().trim(),
				phoneField.getText().trim(),
				emailField.getText().trim(),
				usernameField.getText().trim(),
				activeCheckBox.isSelected()
			);

			showMessage("Personal creado correctamente.");
			clearForm();
			loadStaff();
		} catch (RuntimeException exception) {
			showMessage("Error al crear: " + exception.getMessage());
		}
	}

	@FXML
	private void updateStaff() {
		Staff selectedStaff = getSelectedStaff();
		if (selectedStaff == null) {
			showMessage("Seleccioná un registro para modificar.");
			return;
		}

		try {
			validateForm();
			staffService.update(
				selectedStaff.getId(),
				roleComboBox.getValue(),
				fullNameField.getText().trim(),
				phoneField.getText().trim(),
				emailField.getText().trim(),
				usernameField.getText().trim(),
				activeCheckBox.isSelected()
			);

			showMessage("Personal actualizado correctamente.");
			loadStaff();
		} catch (RuntimeException exception) {
			showMessage("Error al modificar: " + exception.getMessage());
		}
	}

	@FXML
	private void toggleActive() {
		Staff selectedStaff = getSelectedStaff();
		if (selectedStaff == null) {
			showMessage("Seleccioná un registro para dar de alta o baja.");
			return;
		}

		boolean newActiveValue = !Boolean.TRUE.equals(selectedStaff.getIsActive());
		staffService.setActive(selectedStaff.getId(), newActiveValue);
		showMessage(newActiveValue ? "Personal dado de alta." : "Personal dado de baja.");
		loadStaff();
	}

	@FXML
	private void deleteStaff() {
		Staff selectedStaff = getSelectedStaff();
		if (selectedStaff == null) {
			showMessage("Seleccioná un registro para eliminar.");
			return;
		}

		try {
			staffService.delete(selectedStaff.getId());
			showMessage("Personal eliminado correctamente.");
			clearForm();
			loadStaff();
		} catch (RuntimeException exception) {
			showMessage("No se pudo eliminar. Si tiene pedidos asociados, usá baja lógica. Detalle: " + exception.getMessage());
		}
	}

	@FXML
	private void clearForm() {
		staffTable.getSelectionModel().clearSelection();
		roleComboBox.getSelectionModel().select(RoleCode.COOK);
		fullNameField.clear();
		phoneField.clear();
		emailField.clear();
		usernameField.clear();
		activeCheckBox.setSelected(true);
	}

	@FXML
	private void goBack() {
		StageManager.getCurrent().showMainView();
	}

	private void loadStaff() {
		try {
			staffTable.setItems(FXCollections.observableArrayList(staffService.findAll()));
		} catch (RuntimeException exception) {
			showMessage("No se pudo conectar a la base de datos: " + exception.getMessage());
			staffTable.setItems(FXCollections.observableArrayList());
		}
	}

	private void fillForm(Staff staff) {
		if (staff == null) {
			return;
		}

		roleComboBox.getSelectionModel().select(staff.getRole().getRoleCode());
		fullNameField.setText(staff.getFullName());
		phoneField.setText(staff.getPhone());
		emailField.setText(staff.getEmail());
		usernameField.setText(staff.getUsername());
		activeCheckBox.setSelected(Boolean.TRUE.equals(staff.getIsActive()));
	}

	private Staff getSelectedStaff() {
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

	private String formatRole(Staff staff) {
		return switch (staff.getRole().getRoleCode()) {
			case ADMINISTRATOR -> "Administrador";
			case COOK -> "Cocinero";
			case COURIER -> "Delivery";
		};
	}

	private void showMessage(String message) {
		messageLabel.setText(message);
	}
}
