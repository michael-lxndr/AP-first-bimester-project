package org.example.foodflow2.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.foodflow2.model.enums.RolUsuario;

@Entity
@Table(name = "cocineros")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter
@NoArgsConstructor
public class Cocinero extends Usuario {

    @Column(name = "disponible")
    private Boolean disponible = true;

    @Column(name = "especialidad", length = 50)
    private String especialidad;

    @Builder
    public Cocinero(String nombre, String apellido, String cedula,
                    String telefono, String email, String username,
                    String password, String especialidad) {
        super(null, nombre, apellido, cedula, telefono, email, username,
                password, RolUsuario.COCINERO, true, null);
        this.especialidad = especialidad;
    }
}