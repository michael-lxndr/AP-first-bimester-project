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
	private TableView<Personal> tablaPersonal;
	@FXML
	private TableColumn<Personal, Long> columnaId;
	@FXML
	private TableColumn<Personal, String> columnaRol;
	@FXML
	private TableColumn<Personal, String> columnaNombreCompleto;
	@FXML
	private TableColumn<Personal, String> columnaNombreUsuario;
	@FXML
	private TableColumn<Personal, String> columnaCorreo;
	@FXML
	private TableColumn<Personal, String> columnaEstado;
	@FXML
	private ComboBox<CodigoRol> comboRol;
	@FXML
	private TextField campoNombreCompleto;
	@FXML
	private TextField campoTelefono;
	@FXML
	private TextField campoCorreo;
	@FXML
	private TextField campoNombreUsuario;
	@FXML
	private CheckBox checkboxActivo;
	@FXML
	private Label etiquetaMensaje;

	@FXML
	private void initialize() {
		comboRol.setItems(FXCollections.observableArrayList(CodigoRol.values()));
		comboRol.getSelectionModel().select(CodigoRol.COCINERO);
		checkboxActivo.setSelected(true);

		columnaId.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnaNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
		columnaNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
		columnaCorreo.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
		columnaRol.setCellValueFactory(cell -> new SimpleStringProperty(formatearRol(cell.getValue())));
		columnaEstado.setCellValueFactory(cell -> new SimpleStringProperty(Boolean.TRUE.equals(cell.getValue().getActivo()) ? "Activo" : "Inactivo"));

		tablaPersonal.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, personalSeleccionado) -> llenarFormulario(personalSeleccionado));
		cargarPersonal();
	}

	@FXML
	private void crearPersonal() {
		try {
			validarFormulario();
			servicioPersonal.crear(
				comboRol.getValue(),
				campoNombreCompleto.getText().trim(),
				campoTelefono.getText().trim(),
				campoCorreo.getText().trim(),
				campoNombreUsuario.getText().trim(),
				checkboxActivo.isSelected()
			);

			mostrarMensaje("Personal creado correctamente.");
			limpiarFormulario();
			cargarPersonal();
		} catch (RuntimeException exception) {
			mostrarMensaje("Error al crear: " + exception.getMessage());
		}
	}

	@FXML
	private void modificarPersonal() {
		Personal personalSeleccionado = obtenerPersonalSeleccionado();
		if (personalSeleccionado == null) {
			mostrarMensaje("Seleccioná un registro para modificar.");
			return;
		}

		try {
			validarFormulario();
			servicioPersonal.modificar(
				personalSeleccionado.getId(),
				comboRol.getValue(),
				campoNombreCompleto.getText().trim(),
				campoTelefono.getText().trim(),
				campoCorreo.getText().trim(),
				campoNombreUsuario.getText().trim(),
				checkboxActivo.isSelected()
			);

			mostrarMensaje("Personal actualizado correctamente.");
			cargarPersonal();
		} catch (RuntimeException exception) {
			mostrarMensaje("Error al modificar: " + exception.getMessage());
		}
	}

	@FXML
	private void alternarActivo() {
		Personal personalSeleccionado = obtenerPersonalSeleccionado();
		if (personalSeleccionado == null) {
			mostrarMensaje("Seleccioná un registro para dar de alta o baja.");
			return;
		}

		boolean nuevoValorActivo = !Boolean.TRUE.equals(personalSeleccionado.getActivo());
		servicioPersonal.establecerActivo(personalSeleccionado.getId(), nuevoValorActivo);
		mostrarMensaje(nuevoValorActivo ? "Personal dado de alta." : "Personal dado de baja.");
		cargarPersonal();
	}

	@FXML
	private void eliminarPersonal() {
		Personal personalSeleccionado = obtenerPersonalSeleccionado();
		if (personalSeleccionado == null) {
			mostrarMensaje("Seleccioná un registro para eliminar.");
			return;
		}

		try {
			servicioPersonal.eliminar(personalSeleccionado.getId());
			mostrarMensaje("Personal eliminado correctamente.");
			limpiarFormulario();
			cargarPersonal();
		} catch (RuntimeException exception) {
			mostrarMensaje("No se pudo eliminar. Si tiene pedidos asociados, usá baja lógica. Detalle: " + exception.getMessage());
		}
	}

	@FXML
	private void limpiarFormulario() {
		tablaPersonal.getSelectionModel().clearSelection();
		comboRol.getSelectionModel().select(CodigoRol.COCINERO);
		campoNombreCompleto.clear();
		campoTelefono.clear();
		campoCorreo.clear();
		campoNombreUsuario.clear();
		checkboxActivo.setSelected(true);
	}

	@FXML
	private void cerrarVentana(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	private void cargarPersonal() {
		try {
			tablaPersonal.setItems(FXCollections.observableArrayList(servicioPersonal.buscarTodos()));
		} catch (RuntimeException exception) {
			mostrarMensaje("No se pudo conectar a la base de datos: " + exception.getMessage());
			tablaPersonal.setItems(FXCollections.observableArrayList());
		}
	}

	private void llenarFormulario(Personal personal) {
		if (personal == null) {
			return;
		}

		comboRol.getSelectionModel().select(personal.getRol().getCodigoRol());
		campoNombreCompleto.setText(personal.getNombreCompleto());
		campoTelefono.setText(personal.getTelefono());
		campoCorreo.setText(personal.getCorreoElectronico());
		campoNombreUsuario.setText(personal.getNombreUsuario());
		checkboxActivo.setSelected(Boolean.TRUE.equals(personal.getActivo()));
	}

	private Personal obtenerPersonalSeleccionado() {
		return tablaPersonal.getSelectionModel().getSelectedItem();
	}

	private void validarFormulario() {
		if (comboRol.getValue() == null) {
			throw new IllegalArgumentException("El rol es obligatorio.");
		}
		if (campoNombreCompleto.getText().isBlank()) {
			throw new IllegalArgumentException("El nombre completo es obligatorio.");
		}
		if (campoCorreo.getText().isBlank()) {
			throw new IllegalArgumentException("El correo es obligatorio.");
		}
		if (campoNombreUsuario.getText().isBlank()) {
			throw new IllegalArgumentException("El usuario es obligatorio.");
		}
	}

	private String formatearRol(Personal personal) {
		return switch (personal.getRol().getCodigoRol()) {
			case ADMINISTRADOR -> "Administrador";
			case COCINERO -> "Cocinero";
			case REPARTIDOR -> "Repartidor";
		};
	}

	private void mostrarMensaje(String mensaje) {
		etiquetaMensaje.setText(mensaje);
	}
}
