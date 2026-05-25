/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaConfiguracion;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class JPABaseDeDatos {

	private static EntityManagerFactory emf;

	public static synchronized EntityManagerFactory getEntityManagerFactory() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory("RestaurantPU");
		}
		return emf;
	}

	public static synchronized void setEntityManagerFactory(EntityManagerFactory customEmf) {
		emf = customEmf;
	}

}
