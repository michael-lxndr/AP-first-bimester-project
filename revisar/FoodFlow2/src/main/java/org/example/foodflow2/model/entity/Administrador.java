package org.example.foodflow2.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administradores")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    @Column(name = "nivel_acceso", length = 20)
    private String nivelAcceso = "TOTAL";

    @Builder
    public Administrador(String nombre, String apellido, String cedula,
                         String telefono, String email, String username,
                         String password, String nivelAcceso) {
        super(null, nombre, apellido, cedula, telefono, email, username,
                password, org.example.foodflow2.model.enums.RolUsuario.ADMIN, true, null);
        this.nivelAcceso = nivelAcceso;
    }
}