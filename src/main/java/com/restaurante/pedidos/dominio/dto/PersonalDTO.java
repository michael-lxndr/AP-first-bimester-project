package com.restaurante.pedidos.dominio.dto;

/**
 * DTO ligero para mostrar personal en listas/combos de UI.
 */
public record PersonalDTO(
        Long id,
        String nombreCompleto,
        String rol,              // "Cocinero", "Repartidor" (no enum para UI)
        String telefono,
        boolean activo,
        String avatarUrl         // Opcional: para mostrar foto en UI
) {
    public static PersonalDTO fromEntity(
            com.restaurante.pedidos.dominio.entidad.Personal entidad
    ) {
        if (entidad == null) return null;

        return new PersonalDTO(
                entidad.getId(),
                entidad.getNombreCompleto(),
                entidad.getCodigoRol() != null ? entidad.getCodigoRol().getDescripcion() : "Sin rol",
                entidad.getTelefono(),
                entidad.isActivo(),
                null // Avatar se puede agregar después
        );
    }
}