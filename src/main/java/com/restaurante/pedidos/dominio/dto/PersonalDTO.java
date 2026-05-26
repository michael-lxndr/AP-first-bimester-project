package com.restaurante.pedidos.dominio.dto;

import com.restaurante.pedidos.Clases.Personal;
import com.restaurante.pedidos.Clases.Roles;

public record PersonalDTO(
        Long id,
        String nombreCompleto,
        String rol,
        String telefono,
        boolean activo,
        String avatarUrl
) {
    public static PersonalDTO fromEntity(Personal entidad) {
        if (entidad == null) return null;

        String nombreRol = "Sin rol";
        Roles rol = entidad.getRolId();
        if (rol != null && rol.getCodigoRol() != null) {
            nombreRol = rol.getCodigoRol();
        }

        return new PersonalDTO(
                entidad.getPersonalId(),
                entidad.getNombreCompleto(),
                nombreRol,
                entidad.getTelefono(),
                entidad.getActivo(),
                null
        );
    }
}