/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurante.pedidos.LogicaConfiguracion;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Javier Montaño
 */
public class JPABaseDeDatos {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

}
