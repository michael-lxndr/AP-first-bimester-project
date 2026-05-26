/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaServicios;

import com.restaurante.pedidos.Clases.Enums.CodigoRol;
import com.restaurante.pedidos.Clases.Personal;
import com.restaurante.pedidos.Clases.Roles;
import com.restaurante.pedidos.Logica.PersonalJpaController;
import com.restaurante.pedidos.Logica.RolesJpaController;
import com.restaurante.pedidos.LogicaConfiguracion.JPABaseDeDatos;

import java.util.Date;
import java.util.List;


public class ServicioPersonal {

	private final PersonalJpaController personalControlador;
	private final RolesJpaController rolesControlador;

	public ServicioPersonal() {
		personalControlador = new PersonalJpaController(JPABaseDeDatos.getEntityManagerFactory());
		rolesControlador = new RolesJpaController(JPABaseDeDatos.getEntityManagerFactory());
	}

	public List<Personal> buscarTodos() {
		return personalControlador.findPersonalEntities();
	}

	public Personal crear(CodigoRol codigoRol, String nombreCompleto, String telefono, String correoElectronico, String nombreUsuario, boolean activo) {

		Roles rol = buscarRolPorCodigo(codigoRol);

		Personal p = new Personal();
		p.setRolId(rol);
		p.setNombreCompleto(nombreCompleto);
		p.setTelefono(telefono);
		p.setCorreoElectronico(correoElectronico);
		p.setNombreUsuario(nombreUsuario);
		p.setActivo(activo);
		p.setCreadoEn(new Date());

		personalControlador.create(p);

		return p;
	}

	public void modificar(Long id, CodigoRol codigoRol, String nombreCompleto, String telefono, String correo, String usuario, boolean activo) throws Exception {

		Personal p = personalControlador.findPersonal(id);

		if (p == null) {
			throw new IllegalArgumentException("No existe personal ID: " + id);
		}

		p.setRolId(buscarRolPorCodigo(codigoRol));
		p.setNombreCompleto(nombreCompleto);
		p.setTelefono(telefono);
		p.setCorreoElectronico(correo);
		p.setNombreUsuario(usuario);
		p.setActivo(activo);

		personalControlador.edit(p);
	}

	public void eliminar(Long id) throws Exception {
		personalControlador.destroy(id);
	}

	private Roles buscarRolPorCodigo(CodigoRol codigoRol) {

		List<Roles> roles = rolesControlador.findRolesEntities();

		for (Roles r : roles) {
			if (r.getCodigoRol().equals(codigoRol.name())) {
				return r;
			}
		}

		throw new IllegalArgumentException("No existe rol: " + codigoRol);
	}

	public void ejecutarOpcion(int op) {
		switch (op) {
			case 1 -> verPersonal();
			case 2 -> asignarRepartidor();
			default -> {
			}
		}
	}

	private void verPersonal() {
		List<Personal> lista = buscarTodos();
		System.out.println("Total personal: " + lista.size());
	}

	private void asignarRepartidor() {
		System.out.println("Asignación de repartidor ejecutada...");
	}
}
