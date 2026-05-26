package com.restaurante.pedidos.dominio.servicio.ui;

import com.restaurante.pedidos.dominio.dto.PersonalDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IServicioPersonalUI {

    /**
     * Obtiene personal activo filtrado por rol.
     * @param rolDescripcion "Cocinero", "Repartidor", etc.
     */
    CompletableFuture<List<PersonalDTO>> obtenerPersonalPorRol(String rolDescripcion);

    /**
     * Busca personal por nombre (para autocomplete en UI).
     */
    CompletableFuture<List<PersonalDTO>> buscarPersonalPorNombre(String texto);

    /**
     * Actualiza estado de disponibilidad (activo/inactivo).
     */
    CompletableFuture<Boolean> actualizarDisponibilidad(Long personalId, boolean disponible);
}

