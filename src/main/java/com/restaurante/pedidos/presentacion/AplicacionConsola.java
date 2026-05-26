package com.restaurante.pedidos.presentacion;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Aplicación de consola simplificada que maneja el menú de usuario de forma interactiva.
 * Es compatible con flujos de entrada/salida personalizados para pruebas automatizadas.
 */
public class AplicacionConsola {

    public static void ejecutar(InputStream input, PrintStream output, Runnable accionOpcionUno) {
        Scanner scanner = new Scanner(input);
        boolean continuar = true;

        while (continuar && scanner.hasNextLine()) {
            output.println("Seleccione una opción (1: Acción, 0: Salir):");
            String linea = scanner.nextLine().trim();

            if (linea.equals("0")) {
                continuar = false;
            } else if (linea.equals("1")) {
                accionOpcionUno.run();
            } else {
                output.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    public static void main(String[] args) {
        ejecutar(System.in, System.out, () -> {
            System.out.println("Ejecutando acción por defecto...");
        });
    }
}
