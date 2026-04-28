package org.example.foodflow2.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.foodflow2.model.enums.RolUsuario;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id")
@Getter @Setter
@NoArgsConstructor
public class Cliente extends Usuario {

    @Column(name = "fecha_registro")
    private java.time.LocalDate fechaRegistro = java.time.LocalDate.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Direccion> direcciones = new ArrayList<>();

    @Builder
    public Cliente(String nombre, String apellido, String cedula,
                   String telefono, String email, String username, String password) {
        super(null, nombre, apellido, cedula, telefono, email, username,
                password, RolUsuario.CLIENTE, true, null);
    }
}