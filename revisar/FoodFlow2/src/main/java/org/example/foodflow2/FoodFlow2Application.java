package org.example.foodflow2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodFlow2Application {

    public static void main(String[] args) {
        SpringApplication.run(FoodFlow2Application.class, args);
        System.out.println("====================================");
        System.out.println("  FOODFLOW SYSTEM - INICIADO");
        System.out.println("  Tablas creadas en MySQL");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("====================================");
    }

}
