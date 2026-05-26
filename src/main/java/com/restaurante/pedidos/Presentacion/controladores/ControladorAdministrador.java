package com.restaurante.pedidos.presentacion.controladores;

import com.restaurante.pedidos.Clases.Personal;
import com.restaurante.pedidos.Clases.Roles;
import com.restaurante.pedidos.Logica.PersonalJpaController;
import com.restaurante.pedidos.Logica.RolesJpaController;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Date;
import java.util.List;

public class ControladorAdministrador {

    @FXML private TableView<Personal> tablaPersonal;
    @FXML private TableColumn<Personal, Long> columnaId;
    @FXML private TableColumn<Personal, String> columnaRol;
    @FXML private TableColumn<Personal, String> columnaNombreCompleto;
    @FXML private TableColumn<Personal, Boolean> columnaEstado;

    @FXML private ComboBox<String> comboRol;
    @FXML private TextField campoNombreCompleto;
    @FXML private TextField campoTelefono;
    @FXML private TextField campoCorreo;
    @FXML private TextField campoNombreUsuario;
    @FXML private CheckBox checkboxActivo;
    @FXML private Label etiquetaMensaje;

    private PersonalJpaController personalController;
    private RolesJpaController rolesController;
    private ObservableList<Personal> listaPersonal;
    private com.restaurante.pedidos.dominio.servicio.ui.IServicioPersonalUI servicioPersonal;

    public void setServicioPersonal(com.restaurante.pedidos.dominio.servicio.ui.IServicioPersonalUI servicioPersonal) {
        this.servicioPersonal = servicioPersonal;
    }

    public Personal getSeleccionActual() {
        if (tablaPersonal != null) {
            return tablaPersonal.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    @FXML
    public void initialize() {
        comboRol.getItems().addAll("ADMINISTRADOR", "COCINERO", "REPARTIDOR");
        comboRol.getSelectionModel().selectFirst();

        personalController = new PersonalJpaController(JPABaseDeDatos.getEntityManagerFactory());
        rolesController = new RolesJpaController(JPABaseDeDatos.getEntityManagerFactory());
        listaPersonal = FXCollections.observableArrayList();

        // Configurar columnas
        columnaId.setCellValueFactory(new PropertyValueFactory<>("personalId"));
        columnaNombreCompleto.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        columnaEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Columna rol (requiere conversión)
        columnaRol.setCellValueFactory(cellData -> {
            Roles rol = cellData.getValue().getRolId();
            return new javafx.beans.property.SimpleStringProperty(
                    rol != null ? rol.getCodigoRol() : "Sin rol"
            );
        });

        tablaPersonal.setItems(listaPersonal);

        // Cargar datos
        cargarPersonal();

        // Selección de tabla
        tablaPersonal.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        cargarPersonalEnFormulario(selected);
                    }
                }
        );
    }

    private void cargarPersonal() {
        new Thread(() -> {
            try {
                List<Personal> personal = personalController.findPersonalEntities();
                Platform.runLater(() -> {
                    listaPersonal.setAll(personal);
                    etiquetaMensaje.setText("✅ " + personal.size() + " registros cargados");
                });
            } catch (Exception e) {
                Platform.runLater(() -> etiquetaMensaje.setText("❌ Error al cargar: " + e.getMessage()));
            }
        }).start();
    }

    private void cargarPersonalEnFormulario(Personal p) {
        comboRol.setValue(p.getRolId() != null ? p.getRolId().getCodigoRol() : null);
        campoNombreCompleto.setText(p.getNombreCompleto());
        campoTelefono.setText(p.getTelefono());
        campoCorreo.setText(p.getCorreoElectronico());
        campoNombreUsuario.setText(p.getNombreUsuario());
        checkboxActivo.setSelected(p.getActivo());
    }

    @FXML
    private void crearPersonal() {
        String rolSeleccionado = comboRol.getValue();
        if (rolSeleccionado == null || campoNombreCompleto.getText().isEmpty()) {
            etiquetaMensaje.setText("⚠️ Complete los campos obligatorios");
            return;
        }

        new Thread(() -> {
            try {
                Roles rol = buscarRolPorCodigo(rolSeleccionado);
                Personal nuevo = new Personal();
                nuevo.setRolId(rol);
                nuevo.setNombreCompleto(campoNombreCompleto.getText());
                nuevo.setTelefono(campoTelefono.getText());
                nuevo.setCorreoElectronico(campoCorreo.getText());
                nuevo.setNombreUsuario(campoNombreUsuario.getText());
                nuevo.setActivo(checkboxActivo.isSelected());
                nuevo.setCreadoEn(new Date());

                personalController.create(nuevo);
                Platform.runLater(() -> {
                    etiquetaMensaje.setText("✅ Personal creado correctamente");
                    limpiarFormulario();
                    cargarPersonal();
                });
            } catch (Exception e) {
                Platform.runLater(() -> etiquetaMensaje.setText("❌ Error: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void modificarPersonal() {
        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            etiquetaMensaje.setText("⚠️ Seleccione un registro primero");
            return;
        }

        new Thread(() -> {
            try {
                Roles rol = buscarRolPorCodigo(comboRol.getValue());
                seleccionado.setRolId(rol);
                seleccionado.setNombreCompleto(campoNombreCompleto.getText());
                seleccionado.setTelefono(campoTelefono.getText());
                seleccionado.setCorreoElectronico(campoCorreo.getText());
                seleccionado.setNombreUsuario(campoNombreUsuario.getText());
                seleccionado.setActivo(checkboxActivo.isSelected());

                personalController.edit(seleccionado);
                Platform.runLater(() -> {
                    etiquetaMensaje.setText("✅ Personal modificado");
                    cargarPersonal();
                });
            } catch (Exception e) {
                Platform.runLater(() -> etiquetaMensaje.setText("❌ Error: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void alternarActivo() {
        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            etiquetaMensaje.setText("⚠️ Seleccione un registro primero");
            return;
        }

        boolean nuevoEstado = !checkboxActivo.isSelected();
        checkboxActivo.setSelected(nuevoEstado);

        new Thread(() -> {
            try {
                seleccionado.setActivo(nuevoEstado);
                personalController.edit(seleccionado);
                Platform.runLater(() -> {
                    etiquetaMensaje.setText("✅ Estado cambiado a: " + (nuevoEstado ? "Activo" : "Inactivo"));
                    cargarPersonal();
                });
            } catch (Exception e) {
                Platform.runLater(() -> etiquetaMensaje.setText("❌ Error: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void eliminarPersonal() {
        Personal seleccionado = tablaPersonal.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            etiquetaMensaje.setText("⚠️ Seleccione un registro primero");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar");
        confirm.setContentText("¿Eliminar a " + seleccionado.getNombreCompleto() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            new Thread(() -> {
                try {
                    personalController.destroy(seleccionado.getPersonalId());
                    Platform.runLater(() -> {
                        etiquetaMensaje.setText("✅ Personal eliminado");
                        limpiarFormulario();
                        cargarPersonal();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> etiquetaMensaje.setText("❌ Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    @FXML
    private void limpiarFormulario() {
        comboRol.getSelectionModel().clearSelection();
        campoNombreCompleto.clear();
        campoTelefono.clear();
        campoCorreo.clear();
        campoNombreUsuario.clear();
        checkboxActivo.setSelected(true);
        tablaPersonal.getSelectionModel().clearSelection();
        etiquetaMensaje.setText("Formulario limpiado");
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) campoNombreCompleto.getScene().getWindow();
        stage.close();
    }

    private Roles buscarRolPorCodigo(String codigo) {
        for (Roles rol : rolesController.findRolesEntities()) {
            if (rol.getCodigoRol().equals(codigo)) {
                return rol;
            }
        }
        return null;
    }
}