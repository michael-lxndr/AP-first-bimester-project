package com.restaurante.pedidos.mocks;

import com.restaurante.pedidos.dominio.servicio.ui.IServicioPersonalUI;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MockServicioPersonal implements IServicioPersonalUI {

    private static final List<PersonalDTO> PERSONAL_MOCK = List.of(
            new PersonalDTO(1L, "Ana Martínez", "Cocinero", "3001234567", true, null),
            new PersonalDTO(2L, "Luis Ramírez", "Repartidor", "3109876543", true, null),
            new PersonalDTO(3L, "Carla Díaz", "Administrador", "3205551234", true, null),
            new PersonalDTO(4L, "Pedro Sánchez", "Cocinero", "3112223344", false, null)  // inactivo
    );

    @Override
    public CompletableFuture<List<PersonalDTO>> obtenerPersonalPorRol(String rolDescripcion) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            return PERSONAL_MOCK.stream()
                    .filter(p -> p.rol().equalsIgnoreCase(rolDescripcion) && p.activo())
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<List<PersonalDTO>> buscarPersonalPorNombre(String texto) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            if (texto == null || texto.isBlank()) return List.of();

            String query = texto.toLowerCase();
            return PERSONAL_MOCK.stream()
                    .filter(p -> p.nombreCompleto().toLowerCase().contains(query) && p.activo())
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Boolean> actualizarDisponibilidad(Long personalId, boolean disponible) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatencia();
            // Mock: éxito si el ID existe
            return PERSONAL_MOCK.stream().anyMatch(p -> p.id().equals(personalId));
        });
    }

    private void simularLatencia() {
        try {
            Thread.sleep(50 + (long)(Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}